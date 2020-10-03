package com.xd.pre.modules.myeletric.controller;

import com.xd.pre.common.utils.R;
import com.xd.pre.log.annotation.SysOperaLog;
import com.xd.pre.modules.myeletric.domain.MyMeterFee;
import com.xd.pre.modules.myeletric.dto.MyMapFileDto;
import com.xd.pre.modules.myeletric.dto.MyMeterFeeQueryDto;
import com.xd.pre.modules.sys.domain.SysUser;
import com.xd.pre.security.PreSecurityUser;
import com.xd.pre.security.util.SecurityUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/myfile")
public class MyFileController {

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "读取业主的UI图")
    @RequestMapping(value = "/getmapfile", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getMapFile(MyMapFileDto fileDto) {



        //检查传递参数是否正确
        if (null == fileDto)
        {
            return R.error("文件名称错误!");
        }


        PreSecurityUser user = SecurityUtil.getUser();
        if(user == null)
        {
            return R.error("业主信息错误!");
        }



        //检查文件是否存在
        InputStream in = null;
        String sUserID = String.format("%06d",user.getUserId());
        String filePth= "D:\\mapfile\\"+sUserID+"\\"+fileDto.getFile_name();
        System.out.print("读取图形文件:"+filePth+"\n");
        InputStreamReader isr = null;
        BufferedReader br = null;
        try
        {
            File file = new File(filePth);
            if(!file.exists()){

                // 文件不存在
                return R.error("文件不存在!");

            }


            StringBuffer sb = new StringBuffer();
            byte[] tempbytes = new byte[1024];
            int byteread = 0;
            in = new FileInputStream(file);
            String sRet = "";

            MyMapFileDto retDTO = new MyMapFileDto();
            retDTO.setFile_name(fileDto.getFile_name());

            isr = new InputStreamReader(in, "UTF-8");
            br = new BufferedReader(isr);
            String line  = "";

            while ((line = br.readLine()) != null) {
                sRet += line;
            }

            // 读入多个字节到字节数组中，byteread为一次读入的字节数
          /*  while ((byteread = in.read(tempbytes)) != -1) {
                //  System.out.write(tempbytes, 0, byteread);
                String str = new String(tempbytes, 0, byteread);
                sb.append(str);
                sRet += str;
            }*/

            retDTO.setFile_content(sRet);

            //关闭读取的文件流
            in.close();
            isr.close();
            br.close();

            return R.ok(retDTO);
        }
        catch (Exception ex)
        {
            try
            {
                in.close();
                isr.close();
                br.close();
            }
            catch (Exception ex1)
            {

            }
            return R.error("读取文件异常!"+ex.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @SysOperaLog(descrption = "获取用户目录下的图形文件列表")
    @RequestMapping(value = "/getmapfilelist", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R getMapFileList() {

        PreSecurityUser user = SecurityUtil.getUser();
        if(user == null)
        {
            return R.error("业主信息错误!");
        }

        String sUserID = String.format("%06d",user.getUserId());
        String filePth= "D:\\mapfile\\"+sUserID;

        try
        {
            List<MyMapFileDto> fileLst = new ArrayList<MyMapFileDto>();
            File file = new File(filePth);


            // 如果这个路径是文件夹
            if (file.isDirectory()) {
                String[] fileName = file.list();
                for(int i = 0; i < fileName.length;i++)
                {
                    MyMapFileDto fileDto = new MyMapFileDto();
                    fileDto.setFile_name(fileName[i]);
                    fileLst.add(fileDto);
                }

                return  R.ok(fileLst);

            }
            else
            {
                return R.error("业主未配置图形存储空间!");
            }
        }
        catch (Exception ex)
        {
            return R.error("读取业主配置图形文件存储空间错误!"+ex.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('sys:room:view')")
    @RequestMapping(value = "/savemapfile", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public R saveMapFile(MyMapFileDto fileDto) {

        PreSecurityUser user = SecurityUtil.getUser();
        if(user == null)
        {
            return R.error("业主信息错误!");
        }

        String sUserID = String.format("%06d",user.getUserId());
        String filePth= "D:\\mapfile\\"+sUserID;
        FileOutputStream fos = null;
        OutputStreamWriter writer = null;
        try
        {
            File file = new File(filePth);
            if(!file .exists()) {

                return R.error("业主信未配置图形文件存储空间!");
            }

            filePth = filePth+"\\"+fileDto.getFile_name();
            fos = new FileOutputStream(filePth);
            writer = new OutputStreamWriter(fos,"utf-8");
            writer.write(fileDto.getFile_content());
            writer.flush();

           // fos.write(fileDto.getFile_content().getBytes(),);
            writer.close();
            fos.close();


            System.out.print("保存文件成功:"+filePth+"\n");

            return  R.ok("保存成功!");
        }
        catch (Exception ex)
        {
            try
            {
                writer.close();
                fos.close();
            }
            catch (Exception ex1)
            {
                return R.error("保存图形文件失败!"+ex.getMessage());
            }
            return R.error("保存图形文件失败!"+ex.getMessage());
        }
    }

}
