package net.xdclass.video.common;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class VideoStreamController {

    private final ResourceLoader resourceLoader;

    public VideoStreamController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/apione/files/video/{url:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getVideo(
            @PathVariable String url,
            @RequestHeader HttpHeaders headers) throws IOException {

        Resource video = resourceLoader.getResource("file:" + url);

        if (video.exists() && video.isReadable()) {
            long length = video.contentLength();
            List<HttpRange> ranges = headers.getRange();

            if (ranges.isEmpty()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                        .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(length))
                        .body(video);
            }

            HttpRange range = ranges.get(0);
            long start = range.getRangeStart(length);
            long end = range.getRangeEnd(length);

            return ResponseEntity.status(206)
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(end - start + 1))
                    .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + length)
                    .body(video);
        } else {
            return ResponseEntity.status(404).build();
        }
    }
}
