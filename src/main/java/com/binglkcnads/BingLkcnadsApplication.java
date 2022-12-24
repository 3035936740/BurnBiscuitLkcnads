package com.binglkcnads;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@MapperScan("com.binglkcnads.mappers")
@SpringBootApplication
public class BingLkcnadsApplication {
    public static void main(String[] args) {
        SpringApplication.run(BingLkcnadsApplication.class, args);
        System.out.println(
                """
                               .O@@@@@@.                              \s
                             o@@O*....o@@.                            \s
                           .@@*.      ..@@*                 .*@@@@.   \s
                          .@@*. .      ..@@**            *@@@@OOO@@@. \s
                          .@@...   .oo.   o@@@@@@@@@@@@O@@*...    .O@O\s
                           @@. .@@@@.o@   .*********..*OO..         O@*
                            @@@@@     O@    ..****.O@O   ..         *@O
                            .@@.  o@@@@O          oO                o@O
                            @@   O@O  .@O        @@OO@O            .@@\s
                           .@O Oo @@OO@@        o@O  O@           @@O \s
                           .@@*OOO. ..  ..    .* @@@@@.*.  *@@@@@@o   \s
                            *@@        O@  .*  @@    OoOOo @@.        \s
                          ..  @@@*      O@@@@@@@     .*... @@   .Oo   \s
                         O@@@@@oO@@                        @@O@@@@@*  \s
                         .*@O .O@@*                       .@O  .@@.   \s
                           *@O    .                           O@@     \s
                            .@@ .                            @@*      \s
                              O@* .                        @@O        \s
                               o@*                        O@@         \s
                        ===============================================
                        启动成功惹!""");
    }
}
