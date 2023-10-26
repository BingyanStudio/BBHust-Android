package com.bingyan.bbhust.ui.screen.home.main
//
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.heightIn
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.bingyan.bbhust.ui.theme.Gap
//import com.bingyan.bbhust.ui.widgets.LazyImage
//import com.google.accompanist.pager.ExperimentalPagerApi
//import com.google.accompanist.pager.HorizontalPager
//import com.google.accompanist.pager.rememberPagerState
//
//@OptIn(ExperimentalPagerApi::class)
//@Composable
//fun BannerBox(vm: MainViewModel = viewModel()) {
//    val banners = vm.bannersTest
//    val size = banners.size
//    val state = rememberPagerState(Int.MAX_VALUE / 2 / size * size)
//    if (banners.isNotEmpty())
//        HorizontalPager(
//            count = Int.MAX_VALUE,
//            state = state,
//            modifier = Modifier.fillMaxWidth(),
//        ) { page ->
//            val banner = banners[page % size]
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth(0.8f)
//                    .padding(horizontal = Gap.Big, vertical = Gap.Mid)
//            ) {
//                LazyImage(
//                    src = banner, contentDescription = "",
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(Gap.Big))
//                        .heightIn(max = 150.dp),
//                    scale = ContentScale.FillWidth
//                )
//            }
//        }
//}