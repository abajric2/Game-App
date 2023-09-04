package ba.etf.rma23

class GameData {
    companion object {
        fun getAll() : List<Game> {
            return listOf(
           /*     Game("Shattered\nTale of the Forgotten King", "PlayStation 5\nNintendo Switch\nPlayStation 4\nXbox One", "04.06.2019.", 3.7, "shattered", "E10+",
                    "Redlock Studio", "Redlock Studio, Forthright Entertainment",
                    "Role-playing Video Game, Platform game, Indie game, Adventure game, Fighting game, Adventure",
                    "Shattered is a game that mixes platformer and boss-fight phases with deep lore and difficult combat inspired by Souls.",
                    listOf(
                        UserReview("lpa", 1663409640000,
                        "Weird case of a game where it does the easy things poorly, but the hard things well.\n" +
                                "\n" +
                                "If you like soulslikes because of combat, you probably wont like this, but if you enjoy soulslikes " +
                                "because of lore, atmosphere and exploration, you should absolutely give this a try."),
                    UserReview("snurge", 1615044600000,
                    "Recommended with a major caveat:\n" +
                            "\n" +
                            "As a souls game, this game is incredibly weak. The physics of the engine, the fact that every weapon has the same moveset, " +
                            "and the staunch lack of any enemy variety make this really hard to like for a mechanics driven gamer.\n" +
                            "\n" +
                            "HOWEVER, the lore is absolutely stellar, the world is huge and rife with potential for exploration, and there is so much " +
                            "to find and learn if you're into that. If you don't really care about lore, this game is not for you, as the gameplay itself " +
                            "is fine at the best of times."),
                    UserRating("jun_fernandez", 1653883800000, 2.0),
                    UserRating("JasonJacobWhidby", 1621119410000, 5.0),
                    UserRating("shanemash", 1679514600000, 5.0)
                    )),
                Game("Grand Theft Auto V", "PlayStation 3\nXbox 360\nPlayStation 4\nXbox One\nWindows\nPlayStation 5\nXbox Series X/S",
                    "17.09.2013.", 4.4, "gtav", "M",
                    "Rockstar North", "Rockstar Games", "Action-adventure",
                    "Grand Theft Auto V is an action-adventure game played from either a third-person or first-person perspective.",
                    listOf(
                        UserReview("ACisGreatest", 1622850292000,
                        "God Tier Masterpiece in every possible way. Rockstar created something incredible, something ageless, " +
                                "something that gamers in 100 years look back at as revolution of gaming."),
                        UserReview("XGN", 1429227892000,
                        "Rockstar told us the PC-version would be the ultimate version and they were right. GTA 5 on PC looks " +
                                "incredible and it works almost perfectly. " +
                                "Sadly you might still encounter some bugs and glitches in GTA Online that you already saw on the consoles."),
                        UserReview("CGMagazine", 1430523892000,
                        "If GTAV was more fully devoted to being either a comedy or a drama, its narrative may have been more successful. As it is, " +
                                "the most fun to be had in the game comes almost entirely from the moments when the player breaks away from the mission " +
                                "structure and enjoys the world without worrying about the context of its plot."),
                        UserRating("MINECRAFT_ROBLOX_ALL_GAMING", 1617061492000, 5.0),
                        UserRating("jaydeekayex", 1585525492000,4.0)
                    )
                ),
                Game("Resident Evil 4", "PlayStation 4\nXbox One\nNintendo Switch\nPlayStation 2",
                    "11.01.2005.", 4.9, "residentevil4", "M",
                "Capcom, Capcom Production Studio 4, Armature Games", "Capcom", "Survival horror, third-person shooter",
                "In resident evil 4, special agent Leon S. Kennedy is sent on a mission to rescue the U.S. President's daughter who has been kidnapped.",
                listOf()),
                Game("Minecraft", "Windows\nmacOS\nLinux", "18.11.2011.", 5.0,"minecraft", "E10+",
                "Mojang Studios", "Mojang Studios, Xbox Game Studios, Sony Interactive Entertainment",
                "Sandbox, Survival",
                "In Minecraft, players explore a blocky, procedurally generated, three-dimensional world with virtually infinite terrain and may discover and extract raw materials, " +
                        "craft tools and items, and build structures, earthworks, and machines.", listOf()),
                Game("Fortnite", "Windows\nmacOS\nNintendo Switch\nPlayStation 4\nPlayStation 5\nXbox One\nXbox Series X/S" +
                        "\niOS\nAndroid", "25.07.2017.", 3.0, "fortnite", "T",
                "Epic Games", "Epic Games", "Survival, battle royale, sandbox",
                "In Fortnite, players collaborate to survive in an open-world environment, by battling other characters who are controlled either by the game itself, or by other players. " +
                        "The single-player or co-operative (played with friends) mode, called 'Save the World', involves fighting off zombie-like creatures.", listOf()),
                Game("Roblox", "Windows\nmacOS\niOS\nAndroid\nXbox One", "01.09.2006.", 4.0, "roblox",
                "T", "Roblox Corporation", "Roblox Corporation", "Game creation system, massively multiplayer online",
                " Roblox is a global platform where millions of people gather together every day to imagine, create, and share experiences with each " +
                        "other in immersive, user-generated 3D worlds. ", listOf()),
                Game("League of Legends", "Microsoft Windows\nmacOS", "27.10.2009.", 2.0, "lol", "T",
                "Riot Games", "Riot Games", "MOBA",
                    "League of Legends is one of the world's most popular video games, developed by Riot Games. It features a team-based competitive game mode based on strategy and outplaying opponents. " +
                            "Players work with their team to break the enemy Nexus before the enemy team breaks theirs.", listOf()),
                Game("Red Dead Redemption 2", "PlayStation 4\n" +
                        "Xbox One\n" +
                        "Windows\n" +
                        "Stadia", "26.10.2018.", 5.0, "rdr2", "M", "Rockstar Studios", "Rockstar Games",
                    "Action-adventure",
                    "After a robbery goes badly wrong in the western town of Blackwater, Arthur Morgan and the Van der Linde gang are forced to flee. With federal agents and the best bounty hunters in the nation " +
                            "massing on their heels, the gang must rob, steal and fight their way across the rugged heartland of America in order to survive.", listOf()),
                Game("PUBG: Battlegrounds", "Windows\n" +
                        "Android\n" +
                        "iOS\n" +
                        "Xbox One\n" +
                        "PlayStation 4\n" +
                        "Stadia\n" +
                        "Xbox Series X/S\n" +
                        "PlayStation 5", "20.12.2017.", 4.0, "pubg", "T", "PUBG Studios",
                "Krafton\n" +
                        "Microsoft Studios (Xbox One)\n" +
                        "Tencent Games (mobile)", "Battle royale",
                    "PUBG is a player versus player shooter game in which up to one hundred players fight in a battle royale, a type of large-scale last man standing deathmatch where players fight to remain the last alive. " +
                            "Players can choose to enter the match solo, duo, or with a small team of up to four people.", listOf()
                ),
                Game("Mass Effect 2", "Microsoft Windows\n" +
                        "Xbox 360\n" +
                        "PlayStation 3", "26.01.2010.", 4.5, "masseffect", "M", "BioWare",
                "Electronic Arts", "Action role-playing, third-person shooter",
                    "Mass Effect 2 is a single-player action role-playing game in which the player takes the role of Commander Shepard from " +
                            "a third-person perspective. Before the game begins, the player determines Shepard's gender, appearance, military background, combat training, and first name.", listOf()
                )
              */  )
        }
        fun getDetails(title : String) : Game? {
            val games: ArrayList<Game> = arrayListOf()
            games.addAll(getAll())
            val game= games.find { game -> title == game.title }
            if (game != null) return game
            else return null
        }
    }
}