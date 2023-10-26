package com.bingyan.bbhust.ui.widgets


/**
 * 首次启动隐私政策同意弹窗
 */
/*
@Composable
fun StartUp() {
    AnimatedVisibility(visible = state.state.value) {
        Dialog(
            onDismissRequest = {
                //Dismiss
                Msg.state(GlobalState.Idle)
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            )
        ) {
            Box(
                Modifier
                    .clip(CardShapes.medium)
                    .background(colors.background)
                    .padding(Gap.Big)
                    .aspectRatio(0.8f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(top = Gap.Big),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Gap.Big)
                ) {
                    Text(
                        text = string(R.string.policy),
                        color = colors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W700
                    )
                    val text = string(R.string.policy_content)
                    val userAgreementRange =
                        text.indexOfRegexRange("(${string(R.string.user_agreement)})")
                    val privacyPolicyRange =
                        text.indexOfRegexRange("(${string(R.string.privacy_policy)})")
                    val span = AnnotatedString(
                        text,
                        spanStyles = listOf(
                            AnnotatedString.Range(
                                SpanStyle(
                                    color = colors.primary
                                ), userAgreementRange.first, userAgreementRange.last
                            ),
                            AnnotatedString.Range(
                                SpanStyle(
                                    color = colors.primary
                                ), privacyPolicyRange.first, privacyPolicyRange.last
                            ),
                        )
                    )
                    val ctx = LocalContext.current
                    ClickableText(
                        text = span,
                        style = TextStyle(
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            lineHeight = 2.em,
                        ),
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(1f)
                    ) { offset ->
                        when {
                            (userAgreementRange.contains(offset)) -> {
                                Msg.event(
                                    GlobalEvent.Browse(
                                        string(R.string.user_agreement_url),
                                        true
                                    )
                                )
                            }

                            privacyPolicyRange.contains(offset) -> {
                                Msg.event(
                                    GlobalEvent.Browse(
                                        string(R.string.privacy_policy_url),
                                        true
                                    )
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(CardShapes.small)
                            .clickRipple {
                                state.state.value = false
                                state.callback(true)
                            }
                            .background(themeColor)
                            .padding(horizontal = Gap.Big * 4, vertical = Gap.Big),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = string(R.string.agree),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = string(R.string.disagree),
                        fontSize = 12.sp,
                        color = colors.textSecondary,
                        modifier = Modifier.click {
                            state.callback(false)
                        }
                    )
                }
            }
        }
    }
}*/
