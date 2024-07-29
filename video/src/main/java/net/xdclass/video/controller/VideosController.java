package net.xdclass.video.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

@RestController
public class VideosController {

    @GetMapping("/play")
    public void play(HttpServletRequest request, HttpServletResponse response, @RequestParam("url") String videoUrl) throws IOException {
        response.reset(); // 重置响应，清除之前设置的头信息和状态码。

        String baseUrl = "http://localhost:9090/";
        String Url;

        // 移除基本 URL 获取资源路径。
        if (videoUrl.startsWith(baseUrl)) {
            Url = videoUrl.substring(baseUrl.length()); // 去掉基本 URL 得到相对路径。
        } else {
            Url = videoUrl; // 如果 URL 不以 baseUrl 开头，则直接使用给定的 URL。
        }

        // 从类路径加载视频资源。
        ClassPathResource videoResource = new ClassPathResource(Url);

        // 如果资源不存在，则返回 404 状态码。
        if (!videoResource.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File file = videoResource.getFile(); // 获取文件对象。
        long fileLength = file.length(); // 获取文件长度。
        String rangeString = request.getHeader("Range"); // 从请求头中获取 Range 信息。
        long rangeStart = 0; // 默认开始字节。
        long rangeEnd = fileLength - 1; // 默认结束字节（文件的最后一个字节）。

        // 如果 Range 头存在，则解析它以确定请求的字节范围。
        if (rangeString != null && rangeString.startsWith("bytes=")) {
            String[] ranges = rangeString.substring(6).split("-");
            try {
                rangeStart = Long.parseLong(ranges[0]); // 解析开始字节。
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    rangeEnd = Long.parseLong(ranges[1]); // 解析结束字节（如果存在）。
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 如果范围无效，则返回 400 状态码。
                return;
            }
        }

        // 验证解析后的范围以确保其在文件长度范围内。
        if (rangeStart > rangeEnd || rangeEnd >= fileLength) {
            response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE); // 如果范围无效，则返回 416 状态码。
            return;
        }

        long contentLength = rangeEnd - rangeStart + 1; // 计算字节范围的内容长度。

        // 设置必要的响应头以支持部分内容响应。
        response.setHeader("Content-Type", "application/octet-stream"); // 设置内容类型为二进制数据。
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\""); // 为下载建议一个文件名。
        response.setHeader("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength); // 设置内容的字节范围。
        response.setHeader("Accept-Ranges", "bytes"); // 表示服务器接受范围请求。
        response.setHeader("Content-Length", String.valueOf(contentLength)); // 设置字节范围的内容长度。
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 设置 206 状态码以表示部分内容。

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
             OutputStream outputStream = response.getOutputStream()) {
            randomAccessFile.seek(rangeStart); // 移动到范围开始的字节。
            byte[] buffer = new byte[1024 * 1024]; // 创建 1MB 的缓冲区。
            long bytesRead = 0; // 跟踪读取的字节数。

            // 读取并写入指定范围的字节到输出流中。
            while (bytesRead < contentLength) {
                int len = randomAccessFile.read(buffer); // 读取最多 1MB 的数据到缓冲区。
                if (len == -1) {
                    break; // 如果到达文件末尾，则退出循环。
                }
                outputStream.write(buffer, 0, len); // 将缓冲区中的数据写入输出流。
                bytesRead += len; // 更新读取的字节数。
            }
        }
    }
}
