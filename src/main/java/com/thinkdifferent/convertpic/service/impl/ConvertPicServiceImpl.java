package com.thinkdifferent.convertpic.service.impl;

import com.thinkdifferent.convertpic.config.ConvertPicConfig;
import com.thinkdifferent.convertpic.service.ConvertPicService;
import com.thinkdifferent.convertpic.utils.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConvertPicServiceImpl implements ConvertPicService {

    private static Logger log = LoggerFactory.getLogger(ConvertPicServiceImpl.class);

    /**
     * 将传入的JSON对象中记录的文件，转换为JPG，输出到指定的目录中；回调应用系统接口，将数据写回。
     * @param parameters 输入的参数，JSON格式数据对象
     * @return
     */
    public JSONObject ConvertPic(Map<String, Object> parameters) {
        return ConvertPic(parameters, false);
    }

    /**
     * 将传入的JSON对象中记录的文件，转换为JPG，输出到指定的目录中；回调应用系统接口，将数据写回。
     * @param parameters 输入的参数，JSON格式数据对象
     * @param blnJpgTemp 是否存留PDF转换过程中的jpg文件
     */
    public JSONObject ConvertPic(Map<String, Object> parameters, boolean blnJpgTemp){
        JSONObject jsonReturn =  new JSONObject();
        jsonReturn.put("flag", "error");
        jsonReturn.put("message", "Convert Pic to Jpg Error.");

        try{

            /**
             * 输入参数的JSON示例
             *{
             * 	"inputType": "path",
             * 	"inputFile": "D:/cvtest/001.tif",
             * 	"inputHeaders":
             *  {
             *     		"Authorization":"Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0"
             *   },
             * 	"outPutFileName": "001-online",
             * 	"outPutFileType": "jpg",
             * 	"waterMark":
             *   {
             *     		"waterMarkType":"pic",
             *     		"waterMarkFile":"watermark.png",
             *     		"degree":"45",
             *     		"LocateX":"400",
             *     		"LocateY":"600",
             *     		"waterMarkWidth":"100",
             *     		"waterMarkHeight":"100"
             *   },
             *   {
             *     		"waterMarkType":"text",
             *     		"waterMarkText":"内部文件",
             *     		"degree":"45",
             *     		"alpha":"0.7f",
             *     		"fontSize":"90",
             *     		"fontName":"宋体",
             *     		"fontColor":"gray",
             *     		"xMove":"700",
             *     		"yMove":"600"
             *   },
             * 	"writeBackType": "path",
             * 	"writeBack":
             *   {
             *     		"path":"D:/cvtest/"
             *   },
             * 	"writeBackHeaders":
             *   {
             *     		"Authorization":"Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0"
             *   },
             * 	"callBackURL": "http://10.11.12.13/callback"
             * }
             */

            // 输入类型（path/url）
            String strInputType = String.valueOf(parameters.get("inputType"));
            // 输入文件（"D:/cvtest/001.tif"）
            String strInputPath = String.valueOf(parameters.get("inputFile"));
            String strInputPathParam = strInputPath;
            // 默认输入文件存储的临时路径（非path类型时使用）
            String strInPutTempPath = ConvertPicConfig.inPutTempPath;
            strInPutTempPath = strInPutTempPath.replaceAll("\\\\", "/");
            if(!strInPutTempPath.endsWith("/")){
                strInPutTempPath = strInPutTempPath + "/";
            }

            File fileInput = null;

            // 如果输入类型是url，则通过http协议读取文件，写入到默认输出路径中
            if("url".equalsIgnoreCase(strInputType)){
                String strInputFileName = strInputPath.substring(strInputPath.lastIndexOf("/") + 1, strInputPath.length());
                // 检查目标文件夹中是否有重名文件，如果有，先删除。
                fileInput = new File(strInPutTempPath + strInputFileName);
                if(fileInput.exists()){
                    fileInput.delete();
                }

                // 从指定的URL中将文件读取为Byte数组，并写入目标文件
                Map mapInputHeaders = new HashMap<>();
                if(parameters.get("inputHeaders") != null){
                    mapInputHeaders = (Map)parameters.get("inputHeaders");
                }

                byte[] byteFile = GetFileUtil.getFile(strInputPath, mapInputHeaders);
                fileInput = GetFileUtil.byte2File(byteFile, strInPutTempPath + strInputFileName);

                strInputPath = strInPutTempPath + strInputFileName;
            }


            // 转换出来的文件名（不包含扩展名）（"001-online"）
            String strOutPutFileName = String.valueOf(parameters.get("outPutFileName"));
            // 文件输出格式
            String strOutPutFileType = "jpg";
            if(parameters.get("outPutFileType")!=null){
                strOutPutFileType = String.valueOf(parameters.get("outPutFileType"));
            }

            // 文件回写方式（回写路径[path]/回写接口[api]/ftp回写[ftp]）
            String strWriteBackType = "path";

            // 默认输出路径
            String strOutPutPath = ConvertPicConfig.outPutPath;
            strOutPutPath = strOutPutPath.replaceAll("\\\\", "/");
            if(!strOutPutPath.endsWith("/")){
                strOutPutPath = strOutPutPath + "/";
            }

            JSONObject jsonWriteBack = new JSONObject();
            if(parameters.get("writeBackType")!=null){
                strWriteBackType = String.valueOf(parameters.get("writeBackType"));

                // 回写接口或回写路径
                jsonWriteBack = JSONObject.fromObject(parameters.get("writeBack"));
                if("path".equalsIgnoreCase(strWriteBackType)){
                    strOutPutPath = jsonWriteBack.getString("path");

                    strOutPutPath = strOutPutPath.replaceAll("\\\\", "/");
                    if(!strOutPutPath.endsWith("/")){
                        strOutPutPath = strOutPutPath + "/";
                    }
                }
            }


            File fileJpg = null;
            // 将传入的图片文件转换为jpg文件，存放到输出路径中
            if(parameters.containsKey("waterMark")){
                String strJpg = strOutPutPath + strOutPutFileName + ".jpg";
                String strJpgTemp = strOutPutPath + strOutPutFileName + "_temp.jpg";
                File fileJpgTemp = ConvertPicUtil.convertPic2Jpg(strInputPath, strJpgTemp);

                JSONObject jsonWaterMark = JSONObject.fromObject(parameters.get("waterMark"));

                String strWaterMarkType = jsonWaterMark.getString("waterMarkType");
                if("pic".equalsIgnoreCase(strWaterMarkType)){
                    String strWaterMarkFile = System.getProperty("user.dir") + "/watermark/" + jsonWaterMark.getString("waterMarkFile");
                    Integer intDegree = jsonWaterMark.getInt("degree");
                    Integer intLocateX = jsonWaterMark.getInt("LocateX");
                    Integer intLocateY = jsonWaterMark.getInt("LocateY");
                    Integer intWaterMarkWidth = jsonWaterMark.getInt("waterMarkWidth");
                    Integer intWaterMarkHeight = jsonWaterMark.getInt("waterMarkHeight");

                    boolean blnSuccess = WaterMarkUtil.markImageByIcon(strWaterMarkFile, strJpgTemp, strJpg, intDegree,
                            intLocateX, intLocateY, intWaterMarkWidth, intWaterMarkHeight);
                    if(blnSuccess){
                        fileJpgTemp.delete();
                        fileJpg = new File(strJpg);
                    }

                }else if("text".equalsIgnoreCase(strWaterMarkType)){
                    String strWaterMarkText = jsonWaterMark.getString("waterMarkText");
                    Integer intDegree = jsonWaterMark.getInt("degree");
                    String strAlpha = jsonWaterMark.getString("alpha");
                    float floatAlpha = 0.7f;
                    if(strAlpha != null && !"".equals(strAlpha)){
                        floatAlpha = Float.parseFloat(strAlpha);
                    }
                    Integer intFontSize = jsonWaterMark.getInt("fontSize");
                    String strFontName = jsonWaterMark.getString("fontName");
                    String strFontColor = jsonWaterMark.getString("fontColor");
                    Integer intXMove = jsonWaterMark.getInt("xMove");
                    Integer intYMove = jsonWaterMark.getInt("yMove");

                    boolean blnSuccess = WaterMarkUtil.markImageByText(strWaterMarkText, strJpgTemp, strJpg, intDegree,
                            System.getProperty("user.dir"),
                            floatAlpha, intFontSize, strFontName, strFontColor, intXMove, intYMove );
                    if(blnSuccess){
                        fileJpgTemp.delete();
                        fileJpg = new File(strJpg);
                    }

                }

            }else{
                fileJpg = ConvertPicUtil.convertPic2Jpg(strInputPath, strOutPutPath + strOutPutFileName + ".jpg");
            }


            if(fileJpg.exists()){
                log.info("图片文件[" + strInputPathParam + "]转换成功");

                File filePdf = null;
                // 将传入的jpg文件转换为pdf文件，存放到输出路径中
                if(strOutPutFileType.indexOf("pdf")>-1){
                    filePdf = ConvertPicUtil.convertJpg2Pdf(strOutPutPath + strOutPutFileName + ".jpg",
                            strOutPutPath + strOutPutFileName + ".pdf");
                }

                // 如果是通过url方式获取的源文件，在jpg转换完毕后，作为临时文件，已经无用了，可以删掉。
                if("url".equalsIgnoreCase(strInputType)) {
                    if (fileInput.exists()) {
                        fileInput.delete();
                    }
                }

                // 如果“回写类型”不是path，则都需要调用工具进行回写（path直接写入了，不用以下这些处理）
                if(!"path".equalsIgnoreCase(strWriteBackType)){
                   // 回写文件
                    Map mapWriteBackHeaders = new HashMap<>();
                    if(parameters.get("writeBackHeaders") != null){
                        mapWriteBackHeaders = (Map)parameters.get("writeBackHeaders");
                    }

                    if("url".equalsIgnoreCase(strWriteBackType)){
                        String strWriteBackURL = jsonWriteBack.getString("url");
                        if(strOutPutFileType.indexOf("jpg")>-1){
                            jsonReturn = WriteBackUtil.writeBack2Api(strOutPutPath + strOutPutFileName + ".jpg", strWriteBackURL, mapWriteBackHeaders);
                        }
                        if(strOutPutFileType.indexOf("pdf")>-1){
                            jsonReturn = WriteBackUtil.writeBack2Api(strOutPutPath + strOutPutFileName + ".pdf", strWriteBackURL, mapWriteBackHeaders);
                        }
                    }else if("ftp".equalsIgnoreCase(strWriteBackType)){
                        // ftp回写
                        String strFtpHost = jsonWriteBack.getString("host");
                        int intFtpPort = jsonWriteBack.getInt("port");
                        String strFtpUserName = jsonWriteBack.getString("username");
                        String strFtpPassWord = jsonWriteBack.getString("password");
                        String strFtpBasePath = jsonWriteBack.getString("basepath");
                        String strFtpFilePath = jsonWriteBack.getString("filepath");

                        boolean blnFptSuccess = false;
                        if(strOutPutFileType.indexOf("jpg")>-1){
                            FileInputStream in=new FileInputStream(fileJpg);
                            blnFptSuccess = FtpUtil.uploadFile(strFtpHost, intFtpPort, strFtpUserName, strFtpPassWord,
                                    strFtpBasePath, strFtpFilePath, fileJpg.getName(), in);
                        }
                        if(strOutPutFileType.indexOf("pdf")>-1){
                            FileInputStream in=new FileInputStream(filePdf);
                            blnFptSuccess = FtpUtil.uploadFile(strFtpHost, intFtpPort, strFtpUserName, strFtpPassWord,
                                    strFtpBasePath, strFtpFilePath, filePdf.getName(), in);
                        }

                        if(blnFptSuccess){
                            jsonReturn.put("flag", "success");
                            jsonReturn.put("message", "Upload Jpg/Pdf file to FTP success.");
                        }else{
                            jsonReturn.put("flag", "error");
                            jsonReturn.put("message", "Upload Jpg/Pdf file to FTP error.");
                        }

                    }

                    String strFlag = jsonReturn.getString("flag");
                    if("success".equalsIgnoreCase(strFlag)){
                        if(fileJpg.exists()){
                            fileJpg.delete();
                        }
                        if(filePdf.exists()){
                            filePdf.delete();
                        }
                    }

                    // 回调对方系统提供的CallBack方法。
                    if(parameters.get("callBackURL")!=null){
                        String strCallBackURL = String.valueOf(parameters.get("callBackURL"));
                        strCallBackURL = strCallBackURL + "?file=" + strOutPutFileName + "&flag=" + strFlag;

                        WriteBackUtil.sendGet(strCallBackURL);
                    }

                }else{
                    if(!blnJpgTemp && filePdf != null && filePdf.exists()){
                        fileJpg.delete();
                    }

                    jsonReturn.put("flag", "success");
                    jsonReturn.put("message", "Convert Pic to JPG/PDF success.");
                }

            }else{
                log.info("图片文件[" + strInputPathParam + "]转换失败");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return jsonReturn;

    }


}
