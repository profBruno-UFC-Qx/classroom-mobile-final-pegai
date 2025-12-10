package com.pegai.app.ui.screens.login
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.unit.em
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.border
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.tooling.preview.Preview
import com.pegai.app.ui.viewmodel.AuthViewModel

//@Preview(showBackground = true)
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: LoginViewModel = viewModel()
) {

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val camposValidos = email.isNotBlank() && senha.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // ========= ÁREA SUPERIOR ========= //
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(Color.White)
        ) {
            // Ícones externos
            Image(
                painter = painterResource(id = R.drawable.ecosistema),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(90.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.chapeudeformatura),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp)
                    .size(60.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.chip),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(80.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.livro),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(80.dp)
            )

            // Logo + texto
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Bem-Vindo\nAo\nPega Aí",
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 30.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(1.dp))

        // ========= ÁREA INFERIOR ========= //
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            Image(
                painter = painterResource(id = R.drawable.fundo_ondulado),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ===== CAMPO USUÁRIO ===== //
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text(text = "Usuário") },
                    singleLine = true,
                    modifier = Modifier
                        .width(290.dp)
                        .height(52.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedContainerColor = Color(0xFFE8E8E8),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(15.dp))

                // ===== CAMPO SENHA + BOTÃO ===== //
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(

                        value = senha,
                        onValueChange = { senha = it },
                        placeholder = { Text(text = "Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .width(240.dp)
                            .height(52.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFE8E8E8),
                            unfocusedContainerColor = Color(0xFFE8E8E8),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // BOTÃO LOGIN
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (camposValidos) Color(0xFF006BFF)
                                else Color(0xFFBFC4CE)
                            )
                            .clickable(enabled = camposValidos) {
                                viewModel.login(
                                    email,
                                    senha,
                                    onSucess = { user ->
                                        authViewModel.setUsuarioLogado(user)
                                        navController.navigate("Home") {
                                            popUpTo("Login") { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    },
                                    onError = {msg ->

                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Esqueceu a senha?",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.clickable { }
                )

                Spacer(modifier = Modifier.height(25.dp))

                // ===== BOTÃO CRIAR CONTA ===== //
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(Color(0xFF006BFF)),
                    modifier = Modifier
                        .width(260.dp)
                        .height(47.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(text = "CRIAR CONTA", fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ===== BOTÃO GOOGLE ===== //
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .width(260.dp)
                        .height(47.dp),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, Color(0xFFDBDFE9))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}





