package com.fis.lms_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class InternHubLMSServiceApplication {

  static void main(String[] args) {
    SpringApplication.run(InternHubLMSServiceApplication.class, args);
  }

  //    @Bean
  //    public CommandLineRunner testSnowflake(LessonRepository lessonRepository) {
  //        return args -> {
  //            try {
  //                Lesson lesson = new Lesson();
  //                lesson.setName("Bài học Snowflake");
  //                lesson.setIntroduction("Giới thiệu bài học");
  //                lesson.setContent("Nội dung bài học");
  //                lesson.setRequirements("Yêu cầu bài học");
  //                lesson.setLessonImageUrl("https://image.url");
  //
  //                // Lưu vào DB
  //                Lesson saved = lessonRepository.save(lesson);
  //
  //                System.out.println("-----------------------------------------");
  //                System.out.println("LƯU THÀNH CÔNG!");
  //                System.out.println("ID sinh ra bởi Snowflake: " + saved.getLessonId());
  //                System.out.println("-----------------------------------------");
  //            } catch (Exception e) {
  //                e.printStackTrace();
  //            }
  //        };
  //    }
}
