**Convert Pic Service** 

**图片转换Jpg/Pdf服务--测试说明**

# 测试方法

本项目已经集成了SwaggerUI，可以直接访问：http://127.0.0.1:8080/swagger-ui/

在对应接口中输入需要传入的JSON，即可执行测试。

# convert：接收传入参数，生成Jpg/Pdf，按设置回写

## 输入：Path方式

### 输出：Path方式

输入path方式，输出Path方式，即从本地指定位置读取源图片文件，转换后将文件直接写入到指定的目标文件夹，且文件名按照传入的文件名命名。

此种方式适合采用NAS存储附件的方式，可以将公用的NAS挂接到本服务器中，转换后的文件按照业务系统的文件名规范生成新的Jpg/Pdf文件，并存储在指定位置。业务系统可以直接读取转换后的Jpg/Pdf文件在线浏览。

#### 转换为jpg/pdf文件

传入的JSON示例如下：

```json
{
	"inputType": "path",
	"inputFile": "D:/cvtest/001.tif",
	"outPutFileName": "001-online",
	"outPutFileType": "jpg",
	"writeBackType": "path",
	"writeBack": {
		"path": "D:/cvtest/"
	}
}
```

“outPutFileType”设置为“jpg”，则将文件转换为jpg；“outPutFileType”设置为“pdf”，则将文件继续转换为pdf。

转换成功后，接口返回信息如下：

```json
{
  "flag": "success",
  "message": "Convert Pic to JPG/PDF success."
}
```

查看输出文件夹“D:/cvtest/”，即可看到“001-online.jpg”或“001-online.pdf”已生成完毕。

#### 加入图片水印

如果需要在输出的Jpg图片或Pdf文件中加入图片水印，可以在JSON中进行设置。

传入的JSON示例如下：

```json
{
	"inputType": "path",
	"inputFile": "D:/cvtest/001.tif",
	"inputHeaders": {
		"Authorization": "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0"
	},
	"outPutFileName": "001-online",
	"outPutFileType": "jpg",
	"waterMark": {
		"waterMarkType": "pic",
		"waterMarkFile": "watermark.png",
		"degree": "45",
		"LocateX": "400",
		"LocateY": "600",
		"waterMarkWidth": "400",
		"waterMarkHeight": "400"
	},
	"writeBackType": "path",
	"writeBack": {
		"path": "D:/cvtest/"
	}
}
```

“outPutFileType”设置为“jpg”，则将文件转换为jpg；“outPutFileType”设置为“pdf”，则将文件继续转换为pdf。

转换成功后，接口返回信息如下：

```json
{
  "flag": "success",
  "message": "Convert Pic to JPG/PDF success."
}
```

查看输出文件夹“D:/cvtest/”，即可看到“001-online.jpg”或“001-online.pdf”已生成完毕。并且生成的文件中，在指定的坐标位置已经加印了图片水印。

#### 加入文字水印

如果需要在输出的Jpg图片或Pdf文件中加入文字水印，可以在JSON中进行设置。

传入的JSON示例如下：

```json
{
	"inputType": "path",
	"inputFile": "D:/cvtest/001.tif",
	"inputHeaders": {
		"Authorization": "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0"
	},
	"outPutFileName": "001-online",
	"outPutFileType": "jpg",
	"waterMark": {
		"waterMarkType": "text",
		"waterMarkText": "内部文件",
		"degree": "45",
		"alpha": "0.7f",
		"fontSize": "90",
		"fontName": "宋体",
		"fontColor": "gray",
		"xMove": "700",
		"yMove": "600"
	},
	"writeBackType": "path",
	"writeBack": {
		"path": "D:/cvtest/"
	}
}
```

“outPutFileType”设置为“jpg”，则将文件转换为jpg；“outPutFileType”设置为“pdf”，则将文件继续转换为pdf。

转换成功后，接口返回信息如下：

```json
{
  "flag": "success",
  "message": "Convert Pic to JPG/PDF success."
}
```

查看输出文件夹“D:/cvtest/”，即可看到“001-online.jpg”或“001-online.pdf”已生成完毕。并且生成的文件中，在指定的坐标位置已经加印了文字水印。





### 输出：ftp方式

输入path方式，输出ftp方式，即从本地指定位置读取源图片文件，转换后将文件上传到FTP服务器指定的文件夹，且文件名按照传入的文件名命名。

此种方式需要业务系统启动一个FTP服务，将文件存储路径挂接到此FTP服务中，并且为“图片转换服务”开放一个可访问此文件夹的账号。

传入的JSON示例如下：

```json
{
	"inputType": "path",
	"inputFile": "D:/cvtest/001.tif",
	"outPutFileName": "001-online",
	"outPutFileType": "jpg",
	"writeBackType": "ftp",
	"writeBack": {
		"host": "127.0.0.1",
         "port": "21",
         "username": "zz",
         "password": "zz",
         "basepath": "/jpg/",
         "filepath": "/2021/10/"
	}
}
```

“outPutFileType”设置为“jpg”，则将文件转换为jpg；“outPutFileType”设置为“pdf”，则将文件继续转换为pdf。

可以设置“waterMark”，为输出文件加入水印。具体参数见上述章节。

转换成功后，接口返回信息如下：

```json
{
  "flag": "success",
  "message": "Upload Jpg/Pdf file to FTP success."
}
```

查看FTP服务器文件夹“/mp4/2021/10/”，即可看到“001-online.jpg”或“001-online.pdf”已上传完毕。

### 输出：url方式

输入path方式，输出url方式，即从本地指定位置读取源图片文件，转换后调用业务系统提供的“文件上传API”，将文件上传到业务系统中。

此种方式需要业务系统定制开发一个文件上传API接口，并且返回JSON结构的消息。返回信息示例如下：

```json
{
  "flag": "success",
  "message": "Upload Jpg/Pdf file success."
}
```

传入的JSON示例如下：

```json
{
	"inputType": "path",
	"inputFile": "D:/cvtest/001.tif",
	"outPutFileName": "001-online",
	"outPutFileType": "jpg",
	"writeBackType": "url",
	"writeBack": {
		"url": "http://127.0.0.1/upload.do"
	},
	"writeBackHeaders": {
		"Authorization": "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0"
	}
}
```

“outPutFileType”设置为“jpg”，则将文件转换为jpg；“outPutFileType”设置为“pdf”，则将文件继续转换为pdf。

可以设置“waterMark”，为输出文件加入水印。具体参数见上述章节。

转换成功后，接口返回信息如下：

```json
{
  "flag": "success",
  "message": "Upload Jpg/Pdf file success."
}
```



## 输入：url方式

输入url方式，输出Path方式，即从指定的URL地址下载视频文件，转换后将文件直接写入到指定的目标位置（文件夹、ftp、Url），且文件名按照传入的文件名命名。

如果业务系统的附件可以通过http方式直接访问（例如，采用对象存储、S3），则可以使用此种方式获取源图片文件。图片转换服务将文件下载到本地磁盘中后，将格式转换为Jpg/Pdf，再写入到指定的位置。

以输出“path”方式为例，传入的JSON示例如下：

```json
{
	"inputType": "url",
	"inputFile": "http://127.0.0.1/001.MOV",
	"mp4FileName": "001-online",
	"writeBackType": "path",
	"writeBack": {
		"path": "D:/cvtest/"
	}
}
```

## 回调接口

业务系统可以提供一个GET方式的回调接口，在视频文件转换、回写完毕后，本服务可以调用此接口，传回处理的状态。例如：

```http
http://10.11.12.13/callback.do?file=001-online&flag=success
```

在传入JSON参数时，加入如下内容，即可由系统自动回调。

```json
	"callBackURL": "http://10.11.12.13/callback.do"
```

回调接口需要接收两个参数：

- file：处理后的文件名。本例为“001-online”。
- flag：处理后的状态，值为：success 或 error。



# convert2base64：返回转换后的Jpg/Pdf的Base64串

输入方式与上述章节内容相同，支持path、url方式。接口将转换后的文件转为Base64字符串，供业务系统直接接收和使用。

## 转换为jpg/pdf文件

传入的JSON示例如下：

```json
{
	"inputType": "path",
	"inputFile": "D:/cvtest/001.tif",
	"outPutFileName": "001-online",
	"outPutFileType": "jpg"
}
```

“outPutFileType”设置为“jpg”，则将文件转换为jpg；“outPutFileType”设置为“pdf”，则将文件继续转换为pdf。

转换成功后，接口返回信息如下：

```
/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAsLDBkSEw8UHRo…………
```

## 加入图片水印

如果需要在输出的Jpg图片或Pdf文件中加入图片水印，可以在JSON中进行设置。

传入的JSON说明请见上述章节。

## 加入文字水印

如果需要在输出的Jpg图片或Pdf文件中加入文字水印，可以在JSON中进行设置。

传入的JSON说明请见上述章节。



# convert2base64s：返回转换后的Jpg/Pdf的Base64串（2个文件）

输入方式与上述章节内容相同，支持path、url方式。接口将转换后的文件转为Base64字符串，供业务系统直接接收和使用。

## 转换为jpg/pdf文件

传入的JSON示例如下：

```json
{
	"inputType": "path",
	"inputFile": "D:/cvtest/001.tif",
	"outPutFileName": "001-online",
	"outPutFileType": "jpg,pdf"
}
```

“outPutFileType”设置为“jpg”，则将文件转换为jpg；“outPutFileType”设置为“pdf”，则将文件继续转换为pdf。如果设置为“jpg,pdf”，则将转换好的jpg和pdf文件一并转换为Base64输出。

转换成功后，接口返回信息如下：

```json
{
  "flag": "success",
  "message": "Convert Pic to JPG/PDF success.",
  "base64": [
    {
      "filename": "001-online.jpg",
      "base64": "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAs…………"
    },
    {
      "filename": "001-online.pdf",
      "base64": "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAgGBgcGBQgHBwcJCQgKDBQNDAs…………"
    }
 ]
}
```

## 加入图片水印

如果需要在输出的Jpg图片或Pdf文件中加入图片水印，可以在JSON中进行设置。

传入的JSON说明请见上述章节。

## 加入文字水印

如果需要在输出的Jpg图片或Pdf文件中加入文字水印，可以在JSON中进行设置。

传入的JSON说明请见上述章节。

