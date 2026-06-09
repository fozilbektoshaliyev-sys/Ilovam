package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.MediaItem
import com.example.ui.MediaViewModel

@Composable
fun HomeScreen(
    viewModel: MediaViewModel,
    modifier: Modifier = Modifier
) {
    val catalog by viewModel.filteredMediaList.collectAsState()
    val scrollState = rememberScrollState()
    var carouselIndex by remember { mutableStateOf(0) }

    val carouselItems = remember(catalog) {
        catalog.filter { it.type == "Donghua" || it.type == "Anime" }
    }

    if (carouselItems.isNotEmpty()) {
        LaunchedEffect(carouselItems) {
            while (true) {
                delay(4000L)
                carouselIndex = (carouselIndex + 1) % carouselItems.size
            }
        }
    }

    val heroItem = catalog.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Platform Title Info Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val neonCyan = Color(0xFF00E5FF)
                    val neonPink = Color(0xFFFF007F)
                    val neonGold = Color(0xFFFFD700)
                    
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = neonCyan,
                                    shadow = Shadow(
                                        color = neonCyan.copy(alpha = 0.9f),
                                        offset = Offset(0f, 0f),
                                        blurRadius = 18f
                                    )
                                )
                            ) {
                                append("AniManhwa")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = neonPink,
                                    shadow = Shadow(
                                        color = neonPink.copy(alpha = 0.9f),
                                        offset = Offset(0f, 0f),
                                        blurRadius = 18f
                                    )
                                )
                            ) {
                                append("3D")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = neonGold,
                                    shadow = Shadow(
                                        color = neonGold.copy(alpha = 0.9f),
                                        offset = Offset(0f, 0f),
                                        blurRadius = 18f
                                    )
                                )
                            ) {
                                append(" Uz")
                            }
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "3D Anime va Donghua dunyosi",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Visual Notification / Settings Indicator icon
            IconButton(
                onClick = { viewModel.toggleTheme() },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                    .testTag("theme_toggle_home")
            ) {
                val darkTheme by viewModel.darkThemeSelected.collectAsState()
                Icon(
                    imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Mavzu o'zgartirish",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Carousel of Anime/Donghua Posters at the top
        if (carouselItems.isNotEmpty()) {
            val currentHero = carouselItems.getOrNull(carouselIndex) ?: carouselItems[0]
            
            Crossfade(
                targetState = currentHero,
                animationSpec = androidx.compose.animation.core.tween(700),
                label = "DonghuaCarousel"
            ) { hero ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectMedia(hero) }
                        .testTag("hero_banner_card")
                ) {
                    AsyncImage(
                        model = hero.imageUrl,
                        contentDescription = hero.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Shaded Bottom Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                                )
                            )
                    )

                    // Top Action Badges / Indicators
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                            .align(Alignment.TopStart),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = "Ommabop",
                                    modifier = Modifier.size(13.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "PREMERA",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Sliding indicator dots
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            carouselItems.forEachIndexed { idx, _ ->
                                Box(
                                    modifier = Modifier
                                        .size(if (idx == carouselIndex) 7.dp else 4.dp)
                                        .clip(CircleShape)
                                        .background(if (idx == carouselIndex) Color(0xFFFF7A00) else Color.White.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }

                    // Spotlight Title and metadata
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = hero.title,
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = hero.type,
                                color = Color(0xFFFF7A00),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(text = "•", color = Color.White.copy(alpha = 0.5f))
                            Text(
                                text = hero.studio,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(text = "•", color = Color.White.copy(alpha = 0.5f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = hero.rating.toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Fallback spotlight if no carousel items available
            heroItem?.let { hero ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectMedia(hero) }
                        .testTag("hero_banner_card")
                ) {
                    AsyncImage(
                        model = hero.imageUrl,
                        contentDescription = hero.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Shaded Bottom Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                                )
                            )
                    )

                    // Promotional Tag Badge
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Ommabop",
                                modifier = Modifier.size(12.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "KUN SPOTLIGHTI",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Spotlight Title and metadata
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = hero.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = hero.type,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(text = "•", color = Color.White.copy(alpha = 0.5f))
                            Text(
                                text = hero.studio,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                            Text(text = "•", color = Color.White.copy(alpha = 0.5f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = hero.rating.toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 1: Donghua
        MediaSection(
            sectionTitle = "Donghua premeralari",
            leadingIcon = Icons.Default.MovieFilter,
            items = catalog.filter { it.type == "Donghua" },
            accentColor = Color(0xFF0F62FE),
            onItemClicked = { viewModel.selectMedia(it) },
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section 2: Anime
        MediaSection(
            sectionTitle = "Yangi animelar",
            leadingIcon = Icons.Default.Subscriptions,
            items = catalog.filter { it.type == "Anime" },
            accentColor = Color(0xFFFF7A00),
            onItemClicked = { viewModel.selectMedia(it) },
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Section 3: Film
        MediaSection(
            sectionTitle = "Eksklyuziv kinolar",
            leadingIcon = Icons.Default.VideoLibrary,
            items = catalog.filter { it.type == "Film" },
            accentColor = Color(0xFF00B4D8),
            onItemClicked = { viewModel.selectMedia(it) },
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(35.dp))
    }
}

@Composable
fun MediaSection(
    sectionTitle: String,
    leadingIcon: ImageVector,
    items: List<MediaItem>,
    accentColor: Color,
    onItemClicked: (MediaItem) -> Unit,
    viewModel: MediaViewModel
) {
    if (items.isNotEmpty()) {
         Column(modifier = Modifier.fillMaxWidth()) {
             Row(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(horizontal = 20.dp),
                 horizontalArrangement = Arrangement.SpaceBetween,
                 verticalAlignment = Alignment.CenterVertically
             ) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Box(
                         modifier = Modifier
                             .size(28.dp)
                             .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                         contentAlignment = Alignment.Center
                     ) {
                         Icon(
                             imageVector = leadingIcon,
                             contentDescription = null,
                             tint = accentColor,
                             modifier = Modifier.size(16.dp)
                         )
                     }
                     Spacer(modifier = Modifier.width(10.dp))
                     Text(
                         text = sectionTitle,
                         fontSize = 15.sp,
                         fontWeight = FontWeight.Black,
                         color = MaterialTheme.colorScheme.onBackground,
                         letterSpacing = 0.2.sp
                     )
                 }
                
                // Styled Glassy Badge Action
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            if (sectionTitle.contains("Kino")) {
                                viewModel.setTypeFilter("Film")
                            } else if (sectionTitle.contains("Donghua")) {
                                viewModel.setTypeFilter("Donghua")
                            } else {
                                viewModel.setTypeFilter(null)
                            }
                            viewModel.selectTab("qidiruv")
                        }
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Barchasi",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Barchasi",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(items) { item ->
                    MediaMiniCard(item = item, onClick = { onItemClicked(item) })
                }
            }
        }
    }
}

@Composable
fun MediaMiniCard(
    item: MediaItem,
    onClick: () -> Unit
) {
    val accentColor = if (item.isVipOnly) {
        Color(0xFFFFA000) // Diamond VIP glow
    } else {
        Color(0xFF0F62FE) // Standard Uzdigital Blue outline
    }

    Column(
        modifier = Modifier
            .width(135.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .width(135.dp)
                .height(185.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(2.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Transparent dark bottom card gradient for text legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // VIP exclusive premium badge
            if (item.isVipOnly) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF7A00), Color(0xFFFFD54F))
                            ),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .align(Alignment.TopStart)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "VIP",
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "PREMIUM",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Rating small floating glass badge
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
                    .align(Alignment.TopEnd)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = item.rating.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quality Badge lower left
            Surface(
                color = Color(0xFF0F62FE),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = item.type,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // Interactive Play Circle Overlay on bottom right
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(28.dp)
                    .background(Color(0xFFFF7A00), RoundedCornerShape(50.dp))
                    .align(Alignment.BottomEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title and Studio Info
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Tv,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = item.studio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = item.year.take(4),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF7A00).copy(alpha = 0.85f),
                fontSize = 10.sp
            )
        }
    }
}

