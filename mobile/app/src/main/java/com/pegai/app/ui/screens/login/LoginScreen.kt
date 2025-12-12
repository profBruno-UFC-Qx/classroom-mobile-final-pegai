package com.pegai.app.ui.screens.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pegai.app.R
import com.pegai.app.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: LoginViewModel = viewModel()
) {

    // Estados dos campos
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var isFocusedUsuario by remember { mutableStateOf(false) }
    var isFocusedSenha by remember { mutableStateOf(false) }
    val camposValidos = email.isNotBlank() && senha.isNotBlank()

    // ===== PALETA DE CORES =====
    val AzulPrincipal = Color(0xFF1CA3D9)
    val CinzaBorda = Color(0xFFD3D3D3)
    val CinzaTexto = Color(0xFF5A5A5A)

    // ===== CONFIGURAÇÕES FIXAS =====
    val larguraInputs = 280.dp
    val alturaComponente = 60.dp
    val tamanhoBotao = 52.dp
    val espacoEntreBotao = 12.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // ==============================================================
        // 1. HEADER — Botão Voltar
        // ==============================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    // Navega para home com popUpTo
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = CinzaTexto
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Voltar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CinzaTexto
            )
        }

        // ==============================================================
        // 2. ÁREA SUPERIOR — Logo + Ilustrações de Fundo
        // ==============================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(Color.White)
        ) {
            // Imagens decorativas com baixa opacidade
            Image(
                painter = painterResource(id = R.drawable.ecosistema),
                contentDescription = null,
                modifier = Modifier.align(Alignment.TopStart).size(90.dp).alpha(0.2f)
            )
            Image(
                painter = painterResource(id = R.drawable.chapeudeformatura),
                contentDescription = null,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 16.dp).size(60.dp).alpha(0.2f)
            )
            Image(
                painter = painterResource(id = R.drawable.chip),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomStart).size(80.dp).alpha(0.2f)
            )
            Image(
                painter = painterResource(id = R.drawable.livro),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomEnd).size(80.dp).alpha(0.2f)
            )

            // Logo central
            Column(
                modifier = Modifier.align(Alignment.Center).padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Logo",
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Login",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = CinzaTexto
                )
            }
        }

        Spacer(modifier = Modifier.height(1.dp))

        // ==============================================================
        // 3. ÁREA DE FORMULÁRIO — Inputs e Botões
        // ==============================================================
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            // Fundo ondulado
            Image(
                painter = painterResource(id = R.drawable.fundo_ondulado),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 160.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ===== INPUT DE USUÁRIO =====
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            text = "Usuário",
                            color = CinzaTexto.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    singleLine = true,
                    modifier = Modifier
                        .width(larguraInputs)
                        .height(alturaComponente)
                        .onFocusChanged { isFocusedUsuario = it.isFocused }
                        .border(
                            width = 2.dp,
                            color = if (isFocusedUsuario) AzulPrincipal else CinzaBorda,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clip(RoundedCornerShape(20.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = AzulPrincipal
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ===== INPUT SENHA + BOTÃO SETA DE LOGIN =====
                Box(modifier = Modifier.width(larguraInputs)) {

                    TextField(
                        value = senha,
                        onValueChange = { senha = it },
                        placeholder = {
                            Text(
                                text = "Senha",
                                color = CinzaTexto.copy(alpha = 0.6f),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(alturaComponente)
                            .onFocusChanged { isFocusedSenha = it.isFocused }
                            .border(
                                width = 2.dp,
                                color = if (isFocusedSenha) AzulPrincipal else CinzaBorda,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clip(RoundedCornerShape(20.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = AzulPrincipal
                        )
                    )

                    // Botão de entrar (seta)
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = tamanhoBotao + espacoEntreBotao)
                            .size(tamanhoBotao)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (camposValidos) AzulPrincipal else Color(0xFFE0E0E0))
                            .clickable(enabled = camposValidos) {
                                viewModel.login(
                                    email,
                                    senha,
                                    onSucess = { user ->
                                        authViewModel.setUsuarioLogado(user)
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    },
                                    onError = {}
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Entrar",
                            tint = if (camposValidos) Color.White else Color.Gray
                        )
                    }
                }

                // ===== LINK "Esqueceu a senha?" =====
                Box(modifier = Modifier.width(larguraInputs).padding(top = 8.dp)) {
                    Text(
                        text = "Esqueceu a senha?",
                        fontSize = 12.sp,
                        color = CinzaTexto,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart).clickable { }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ===== BOTÃO CRIAR CONTA =====
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(AzulPrincipal),
                    modifier = Modifier.width(larguraInputs).height(48.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(text = "Criar Conta", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== BOTÃO LOGIN COM GOOGLE =====
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.width(larguraInputs).height(48.dp),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black,
                        containerColor = Color.White
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Entrar com Google", fontSize = 14.sp)
                }
            }
        }
    }
}
