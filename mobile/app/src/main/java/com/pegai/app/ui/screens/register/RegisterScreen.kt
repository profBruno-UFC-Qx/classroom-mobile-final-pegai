package com.pegai.app.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.pegai.app.R

@Composable
fun RegisterScreen(navController: NavController) {
    val VerdePrincipal = Color(0xFF88D888)
    val CinzaBorda = Color(0xFFD3D3D3)
    val CinzaTexto = Color(0xFF5A5A5A)
    val CinzaInputBackground = Color(0xFFF5F5F5)

    val gradienteBotao = Brush.linearGradient(
        colors = listOf(
            Color(0xFFAAD87A),
            Color(0xFF88D888),
            Color(0xFF66E0B8)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val corDesabilitado = Color(0xFFBFC4CE)

    val larguraInputs = 350.dp
    val alturaComponente = 60.dp

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

    var isFocusedNome by remember { mutableStateOf(false) }
    var isFocusedEmail by remember { mutableStateOf(false) }
    var isFocusedWpp by remember { mutableStateOf(false) }
    var isFocusedSenha by remember { mutableStateOf(false) }
    var isFocusedConfSenha by remember { mutableStateOf(false) }

    val camposValidos = true

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.blobs1),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(180.dp)
                .height(180.dp)
                .align(Alignment.TopStart)
                .offset(x = (0).dp, y = (30).dp)
        )

        Image(
            painter = painterResource(id = R.drawable.blobs2),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(110.dp)
                .height(110.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (0).dp, y = (25).dp)
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    .shadow(elevation = 4.dp, shape = RectangleShape, spotColor = Color.Gray, ambientColor = Color.Gray)
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = CinzaTexto)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Criar Conta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CinzaTexto)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    modifier = Modifier
                        .width(larguraInputs)
                        .padding(bottom = 30.dp, top = 20.dp)
                ) {
                    Text(text = "Vamos começar,", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = "         Criando seu perfil!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Column(modifier = Modifier.width(larguraInputs)) {
                    Text("Nome Completo", color = CinzaTexto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                    TextField(
                        value = nome, onValueChange = { nome = it },
                        placeholder = {
                            Text(
                                "Ex: João da Silva",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth().height(alturaComponente).onFocusChanged { isFocusedNome = it.isFocused }
                            .border(2.dp, if (isFocusedNome) VerdePrincipal else CinzaBorda, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp)),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = CinzaInputBackground, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.width(larguraInputs)) {
                    Text("Email", color = CinzaTexto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                    TextField(
                        value = email, onValueChange = { email = it },
                        placeholder = {
                            Text(
                                "Ex: exemplo@email.com",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth().height(alturaComponente).onFocusChanged { isFocusedEmail = it.isFocused }
                            .border(2.dp, if (isFocusedEmail) VerdePrincipal else CinzaBorda, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp)),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = CinzaInputBackground, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.width(larguraInputs)) {
                    Text("WhatsApp", color = CinzaTexto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                    TextField(
                        value = whatsapp, onValueChange = { whatsapp = it },
                        placeholder = {
                            Text(
                                "Ex: (88) 99999-9999",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth().height(alturaComponente).onFocusChanged { isFocusedWpp = it.isFocused }
                            .border(2.dp, if (isFocusedWpp) VerdePrincipal else CinzaBorda, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp)),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = CinzaInputBackground, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.width(larguraInputs)) {
                    Text("Senha", color = CinzaTexto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                    TextField(
                        value = senha, onValueChange = { senha = it },
                        placeholder = {
                            Text(
                                "******",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true, visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth().height(alturaComponente).onFocusChanged { isFocusedSenha = it.isFocused }
                            .border(2.dp, if (isFocusedSenha) VerdePrincipal else CinzaBorda, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp)),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = CinzaInputBackground, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.width(larguraInputs)) {
                    Text("Confirmar Senha", color = CinzaTexto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
                    TextField(
                        value = confirmarSenha, onValueChange = { confirmarSenha = it },
                        placeholder = {
                            Text(
                                "******",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        },
                        singleLine = true, visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        modifier = Modifier.fillMaxWidth().height(alturaComponente).onFocusChanged { isFocusedConfSenha = it.isFocused }
                            .border(2.dp, if (isFocusedConfSenha) VerdePrincipal else CinzaBorda, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp)),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = CinzaInputBackground, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .width(larguraInputs)
                        .height(48.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(
                            if (camposValidos) gradienteBotao else SolidColor(corDesabilitado)
                        )
                        .clickable(enabled = camposValidos) {
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Criar Conta",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}