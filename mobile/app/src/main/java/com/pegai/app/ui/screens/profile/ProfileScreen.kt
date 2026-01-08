package com.pegai.app.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.R
import com.pegai.app.ui.components.GuestPlaceholder
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.profile.ProfileViewModel
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.nativeCanvas

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: ProfileViewModel = viewModel()
) {
    val authUser by authViewModel.usuarioLogado.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showPixManagerDialog by remember { mutableStateOf(false) }
    var tempChavePix by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { viewModel.atualizarFotoDePerfil(it) } }
    )

    // Usando o gradiente dinâmico do tema
    val mainGradient = brandGradient()

    LaunchedEffect(authUser) {
        viewModel.carregarDadosUsuario(authUser)
    }

    if (authUser == null) {
        GuestPlaceholder(
            title = "Acesse seu Perfil",
            subtitle = "Faça login para gerenciar seus dados e ver sua reputação.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background) // Fundo dinâmico
                    .verticalScroll(rememberScrollState())
            ) {
                // --- CABEÇALHO ---
                Box(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                            .background(mainGradient)
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            "Meu Perfil",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pix
                            Column(
                                modifier = Modifier.weight(1f).clickable {
                                    tempChavePix = uiState.chavePix
                                    showPixManagerDialog = true
                                },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_qrcode_pix),
                                        contentDescription = "Pix",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Chave PIX", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Foto
                            Column(
                                modifier = Modifier.weight(1.8f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    Box(
                                        modifier = Modifier.size(110.dp).clip(CircleShape).background(Color.White).padding(3.dp).clip(CircleShape)
                                    ) {
                                        if (uiState.isLoading) {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                CircularProgressIndicator(modifier = Modifier.size(30.dp), color = MaterialTheme.colorScheme.primary)
                                            }
                                        } else if (uiState.user?.fotoUrl?.isNotEmpty() == true) {
                                            AsyncImage(
                                                model = uiState.user?.fotoUrl,
                                                contentDescription = "Foto",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = uiState.user?.nome?.first()?.toString() ?: "U",
                                                    fontSize = 40.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }

                                    Surface(
                                        modifier = Modifier.size(32.dp).offset(x = (-4).dp, y = (-4).dp).clickable {
                                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        },
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary,
                                        border = BorderStroke(2.dp, Color.White),
                                        shadowElevation = 4.dp
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }

                            // Logout
                            Column(
                                modifier = Modifier.weight(1f).clickable { authViewModel.logout() },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Sair", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = uiState.user?.nome ?: "Usuário",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.user?.email ?: "",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }

                // --- CARDS DE STATUS ---
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-60).dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatusCard(
                            modifier = Modifier.weight(1f),
                            label = "Locador",
                            value = String.format("%.1f ★", uiState.user?.notaLocador ?: 5.0),
                            subtext = "${uiState.user?.totalAvaliacoesLocador ?: 0} avaliações",
                            icon = Icons.Default.Store
                        )
                        StatusCard(
                            modifier = Modifier.weight(1f),
                            label = "Locatário",
                            value = String.format("%.1f ★", uiState.user?.notaLocatario ?: 5.0),
                            subtext = "${uiState.user?.totalAvaliacoesLocatario ?: 0} avaliações",
                            icon = Icons.Default.ShoppingBag
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatusCard(
                            modifier = Modifier.weight(1f),
                            label = "Aluguéis",
                            value = uiState.alugueis,
                            subtext = "Realizados",
                            icon = Icons.Default.SwapHoriz
                        )
                        StatusCard(
                            modifier = Modifier.weight(1f),
                            label = "Anúncios",
                            value = uiState.anuncios,
                            subtext = "Ativos",
                            icon = Icons.Default.Campaign
                        )
                    }
                }

                // --- MENU ---
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).offset(y = (-40).dp)
                ) {
                    ProfileMenuItem(Icons.Default.Person, "Meus Dados Pessoais", { })
                    ProfileMenuItem(Icons.Default.History, "Histórico de Empréstimos", { })
                    ProfileMenuItem(Icons.Default.Settings, "Configurações", { })
                    ProfileMenuItem(Icons.Default.Info, "Ajuda e Suporte", { })
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            // --- DIÁLOGO PIX ---
            // --- DIÁLOGO PIX (ESTILO SÓLIDO E MODERNO) ---
            if (showPixManagerDialog) {
                Dialog(onDismissRequest = { showPixManagerDialog = false }) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = MaterialTheme.colorScheme.surface, // Branco no Light, Cinza Escuro no Dark
                        tonalElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Header do Modal
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_qrcode_pix),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Recebimento via PIX",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "Sua chave será usada para gerar o QR Code de pagamento dos seus aluguéis.",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Campo de Chave
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Sua Chave Pix",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                                )
                                TextField(
                                    value = tempChavePix,
                                    onValueChange = { tempChavePix = it },
                                    placeholder = { Text("CPF, e-mail ou chave aleatória") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = getFieldColor(),
                                        unfocusedContainerColor = getFieldColor(),
                                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    viewModel.atualizarChaveTemp(tempChavePix)
                                    viewModel.salvarChavePix()
                                    showPixManagerDialog = false
                                },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Atualizar Chave", fontWeight = FontWeight.Bold)
                            }

                            // Área do QR Code
                            if (uiState.chavePix.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(32.dp))

                                Text(
                                    text = "QR Code de Recebimento",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Card do QR Code com fundo branco sólido para leitura
                                Surface(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                                    color = Color.White,
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                                        AsyncImage(
                                            model = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${uiState.chavePix}",
                                            contentDescription = "QR Code Pix",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            TextButton(onClick = { showPixManagerDialog = false }) {
                                Text("Agora não", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subtext: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier.height(125.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 12.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center, maxLines = 1)
                Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), textAlign = TextAlign.Center, maxLines = 1)
                Text(subtext, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), textAlign = TextAlign.Center, lineHeight = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
        }
    }
}