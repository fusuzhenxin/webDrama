package net.xdclass.video.controller;
import net.xdclass.video.common.Result;
import net.xdclass.video.mapper.AcquireMapper;
import net.xdclass.video.service.AcquireService;
import net.xdclass.video.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;
    @Autowired
    private AcquireMapper acquireMapper;
    @Autowired
    private AcquireService acquireService;
    @Autowired
    private RedisTemplate redisTemplate;
    //获取这个剧有多少集
    @GetMapping("/apiOne/videos/diversity")
    public Result diversitys(@RequestParam String name){
       Integer diversity= videoService.seleteDiversitys(name);
       return Result.success(diversity);
    }

    //获取每集url
    @GetMapping("/apiOne/videos/{videoName}/episode/{episodeNumber}")
    public Result getEpisodeUrl(@PathVariable String videoName, @PathVariable Integer episodeNumber) {
        String EpisodeUrl="EpisodeUrl:"+videoName+":"+"第"+episodeNumber+"集";

        Object  EpisodeUrlList= redisTemplate.opsForValue().get(EpisodeUrl);
        if (EpisodeUrlList == null){
            // 根据 videoName 和 episodeNumber 获取视频的 URL
            String videoUrl = videoService.seleteEpisodeUrl(videoName, episodeNumber);
            redisTemplate.opsForValue().set(EpisodeUrl,EpisodeUrlList);
            return Result.success(videoUrl);
        }
//        // 根据 videoName 和 episodeNumber 获取视频的 URL
//        String videoUrl = videoService.seleteEpisodeUrl(videoName, episodeNumber);
//
//        if (videoUrl == null) {
//            return Result.error("Video not found");
//        }
//        return Result.success(videoUrl);
        return Result.success(EpisodeUrlList);
    }

    //除了短剧走这个爬虫接口
//    @GetMapping("/save")
//    public Result save(@RequestParam String name,String classify){
//        videoService.saveList(name,classify);
//        return Result.success();
//    }

//    @GetMapping("/startDownload")
//    public String startDownload(@RequestParam String name,@RequestParam String classify) {
//        String taskId = UUID.randomUUID().toString();
//        new Thread(() -> {
//            try {
//                videoService.saveList(name,classify,taskId);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//        return taskId;
//    }
//    @GetMapping("/startDownloadOnyWay")
//    public String startDownloadOnyWay(@RequestParam String name,@RequestParam String classify) {
//        String taskId = UUID.randomUUID().toString();
//        new Thread(() -> {
//            try {
//                videoService.saveListOneway(name,classify,taskId);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//        return taskId;
//    }
//    //短剧
//    @GetMapping("/startDownloads")
//    public String startDownload(@RequestParam String name) {
//        String taskId = UUID.randomUUID().toString();
//        new Thread(() -> {
//            try {
//                videoService.saves(name, taskId);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//        return taskId;
//    }
//
//
//    @GetMapping("/progress")
//    public SseEmitter progress(@RequestParam String taskId) {
//        SseEmitter emitter = new SseEmitter();
//        new Thread(() -> {
//            try {
//                while (true) {
//                    Double progress = DownloadProgressManager.getProgress(taskId);
//                    if (progress != null) {
//                        emitter.send(progress);
//                        if (progress >= 100) {
//                            DownloadProgressManager.setProgress(taskId, 0);
//                        }
//                    }
//                    Thread.sleep(10); // 每秒发送一次进度
//                }
//            } catch (Exception e) {
//                emitter.completeWithError(e);
//            }
//        }).start();
//        return emitter;
//    }

    //短剧的爬虫接口
//    @GetMapping("/saves")
//    public Result url(@RequestParam String name){
//        videoService.saves(name);
//        return Result.success();
//    }

//    @GetMapping("/acquire")
//    public Result acquireList(@RequestParam String name){
//        List<Details> diversity= videoService.seleteAcquireList(name);
//        return Result.success(diversity);
//    }

//    //把短剧详情信息全部存数据库
//    @GetMapping("/acquire")
//    public Result acquireList(){
//        List<Acquire> diversity= videoService.seleteAcquireLists();
//        return Result.success(diversity);
//    }
//    //搜索框爬取其他剧详情
//    @GetMapping("/saveListAcquire")
//    public Result saveListAcquire(@RequestParam String name,@RequestParam String classify){
//        List<Acquire> diversity= videoService.saveListAcquire(name,classify);
//        return Result.success(diversity);
//    }

    // //爬虫搜索短剧视频
//    @GetMapping("/page")
//    public Result findPage(@RequestParam(defaultValue = "") String name,
//                           @RequestParam Integer pageNum,
//                           @RequestParam Integer pageSize) {
//        //QueryWrapper 来构建查询条件，并基于条件执行了分页查询
//        QueryWrapper<Acquire> queryWrapper = new QueryWrapper<>();
//        queryWrapper.orderByDesc("id");
//        if (!"".equals(name)) {
//            queryWrapper.like("name", name);
//        }
//        Page<Acquire> page = acquireService.page(new Page<>(pageNum, pageSize), queryWrapper);
//        return Result.success(page);
//    }
}
