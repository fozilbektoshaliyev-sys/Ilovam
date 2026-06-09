package com.example.data

import java.io.Serializable

data class MediaItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val videoUrl: String, // Simulated streaming URL
    val type: String, // "Anime", "Donghua", "Film"
    val duration: String,
    val fileSize: String,
    val rating: Double,
    val studio: String,
    val year: String,
    val genre: List<String>,
    val isVipOnly: Boolean = false,
    val progress: Float = 0f // Continue watching
) : Serializable {
    companion object {
        val mockData = listOf(
            MediaItem(
                id = "1",
                title = "Soul Land (Janglar Hududi)",
                type = "Donghua",
                imageUrl = "https://images.unsplash.com/photo-1541562232579-512a21360020?w=600&auto=format&fit=crop&q=60",
                videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                duration = "24 daqiqa",
                fileSize = "210 MB",
                rating = 4.9,
                studio = "Sparkly Key Animation",
                year = "2018-2024",
                genre = listOf("Kultivatsiya", "Sarguzasht", "Jangari", "3D"),
                isVipOnly = true,
                description = "Tang San o'zining o'tmishdagi hayoti va yashirin qurol bilimlarini saqlab qolgan holda Douluo Dalu dunyosida qayta tug'iladi. Bu dunyoda har bir inson o'z ruhiga ega bo'lib, Tang San o'zining zaif hisoblangan ko'k o't ruhi bilan buyuk usta bo'lish yo'lini boshlaydi."
            ),
            MediaItem(
                id = "2",
                title = "Battle Through the Heavens (Samoviy Amaldor)",
                type = "Donghua",
                imageUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600&auto=format&fit=crop&q=60",
                videoUrl = "https://www.w3schools.com/html/movie.mp4",
                duration = "22 daqiqa",
                fileSize = "185 MB",
                rating = 4.8,
                studio = "Shanghai Motion Magic",
                year = "2017-2024",
                genre = listOf("Jangari", "Sehrli", "Kultivatsiya", "3D"),
                description = "Xiao Yan o'zining mislsiz iste'dodini yo'qotadi va barchaning masaxarasiga qoladi. Ammo uning uzugidagi qadimgi ruh Yao Lao unga yangi umid beradi va Xiao Yan olovlar qiroli bo'lish uchun o'z sarguzashtini boshlaydi."
            ),
            MediaItem(
                id = "3",
                title = "Perfect World (Mukammal Dunyo)",
                type = "Donghua",
                imageUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=60",
                videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                duration = "25 daqiqa",
                fileSize = "230 MB",
                rating = 4.7,
                studio = "Shanghai Foch Film",
                year = "2021-2024",
                genre = listOf("Sarguzasht", "Kultivatsiya", "Mifologiya", "3D"),
                isVipOnly = true,
                description = "Shi Hao yoshligida o'zining oliy suyagi tortib olinganidan keyin cho'l qishlog'ida o'sadi. U o'zining haqiqiy kelib chiqishini aniqlash va koinotni larzaga keltirgan xavflarga qarshi turish uchun kuch to'playdi."
            ),
            MediaItem(
                id = "4",
                title = "Demon Slayer: Mugen Train",
                type = "Film",
                imageUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=60",
                videoUrl = "https://media.w3.org/2010/05/sintel/trailer_hd.mp4",
                duration = "1 soat 57 daqiqa",
                fileSize = "1.2 GB",
                rating = 4.9,
                studio = "Ufotable Studio",
                year = "2020",
                genre = listOf("Jangari", "Tarixiy", "Drama", "Anime"),
                description = "Tanjiro va uning do'stlari Mugen poyezdida sodir bo'layotgan sirli g'oyib bo'lish holatlarini tekshirish uchun olov ustuni Kyojuro Rengoku bilan birga safarga chiqadilar."
            ),
            MediaItem(
                id = "5",
                title = "Throne of Seal (Muhrlangan Taxt)",
                type = "Donghua",
                imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=60",
                videoUrl = "https://www.w3schools.com/html/movie.mp4",
                duration = "21 daqiqa",
                fileSize = "190 MB",
                rating = 4.8,
                studio = "Shenying Studio",
                year = "2022-2024",
                genre = listOf("Jangari", "Fantastika", "Ritsarlar", "3D"),
                description = "Insoniyat jinlar imperiyasi tomonidan bosib olingan dunyoda o'z yashashini himoya qilishga urinmoqda. Long Haochen ismli yosh mard yigit o'z onasini qutqarish uchun ritsarlar akademiyasiga kiradi va muqaddas ritsarga aylanadi."
            ),
            MediaItem(
                id = "6",
                title = "Land of the Lustrous (Qimmatbaho Toshlar)",
                type = "Anime",
                imageUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?w=600&auto=format&fit=crop&q=60",
                videoUrl = "https://media.w3.org/2010/05/sintel/trailer_hd.mp4",
                duration = "24 daqiqa",
                fileSize = "200 MB",
                rating = 4.6,
                studio = "Orange Studio (3D)",
                year = "2017",
                genre = listOf("Sarguzasht", "Drama", "Toshlar", "CGI 3D"),
                description = "Kelajakda Yer yuzida 28 ta tirik qimmatbaho metall va tosh jonzotlar yashaydi. Ular oyda yashovchi mistik turlardan saqlanish uchun jang qilishadi. Eng zaif a'zosi Phosphophyllite xronika yozishga tayinlanadi va sirlarni ocha boshlaydi."
            ),
            MediaItem(
                id = "7",
                title = "Spirited Away (Ruhlar Chegarasida)",
                type = "Film",
                imageUrl = "https://images.unsplash.com/photo-1501183007986-d0d080b147f9?w=600&auto=format&fit=crop&q=60",
                videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                duration = "2 soat 5 daqiqa",
                fileSize = "1.4 GB",
                rating = 4.9,
                studio = "Studio Ghibli",
                year = "2001",
                genre = listOf("Sarguzasht", "Sehrli", "Oila", "Klassika"),
                description = "Chixiro ismli 10 yoshli qizaloq ota-onasi bilan sirli dunyoga tushib qoladi. Otasi va onasi cho'chqaga aylanib qolganidan so'ng, u o'z oilasini qutqarish va insonlar dunyosiga qaytish uchun ruhlar saunasida ishlay boshlaydi."
            )
        )
    }
}
