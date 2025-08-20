package com.gabriel.danael_fernandes_react_manager

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.net.URL

@SpringBootTest
class YoutubeClientApiIntegrationTest{
    @Autowired
    lateinit var youtubeClientApi: YoutubeClientApi


    @Test
    fun aaa(){
        youtubeClientApi.createPlaylist("aaaaaaaaaa")
    }
    @Test
    fun must_find_video(){

        val result = youtubeClientApi.searchVideo(URL("https://www.youtube.com/watch?v=xAaGxhDiGg8"))

        println(result)
    }


}