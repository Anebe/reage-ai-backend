package com.gabriel.danael_fernandes_react_manager

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
@SpringBootTest
class YoutubeGatewayIntegrationTest{
    @Autowired
    lateinit var youtubeGateway: YoutubeGateway

    @Test
    fun must_find_video(){

        val result = youtubeGateway.searchVideo("https://www.youtube.com/watch?v=xAaGxhDiGg8")

        println(result)
    }


}