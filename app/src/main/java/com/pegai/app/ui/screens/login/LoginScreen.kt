package com.pegai.app.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/login/LoginScreen.kt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
=======
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/login/LoginScreen.kt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/login/LoginScreen.kt
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pegai.app.R
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
import com.pegai.app.ui.viewmodel.AuthViewModel
=======
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pegai.app.R
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.login.LoginViewModel
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/login/LoginScreen.kt

@Composable
fun LoginScreen(
    navController: NavController,
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/login/LoginScreen.kt
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val isLoading by authViewModel.isLoading.collectAsState()
    val erro by authViewModel.erro.collectAsState()

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    LaunchedEffect(erro) {
        if (erro != null) {
            Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brandGradient())
    ) {
        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Botão Voltar ---
                Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(4.dp, CircleShape),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
=======
    authViewModel: AuthViewModel,
    // Injeção do ViewModel
    viewModel: LoginViewModel = viewModel()
) {
    // Observando o Estado (MVVM)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Estados locais (apenas para controle visual dos campos)
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var isFocusedUsuario by remember { mutableStateOf(false) }
    var isFocusedSenha by remember { mutableStateOf(false) }

    // Validação visual
    val camposValidos = email.isNotBlank() && senha.isNotBlank()

    // Reagindo a Mudanças de Estado (Sucesso ou Erro)
    LaunchedEffect(uiState) {
        if (uiState.erro != null) {
            Toast.makeText(context, uiState.erro, Toast.LENGTH_LONG).show()
        }

        if (uiState.loginSucesso) {
            // Passa o usuário logado para o AuthViewModel global
            uiState.usuario?.let { authViewModel.setUsuarioLogado(it) }

            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // ===== PALETA DE CORES =====
    val AzulPrincipal = Color(0xFF1CA3D9)
    val CinzaBorda = Color(0xFFD3D3D3)
    val CinzaTexto = Color(0xFF5A5A5A)

    // ===== GRADIENTE PRINCIPAL =====
    val mainGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0A5C8A), // Azul escuro
            Color(0xFF0E8FC6), // Azul médio
            Color(0xFF2ED1B2)  // Verde água
        )
    )

    // ===== CONFIGURAÇÕES DE TAMANHO =====
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
        // 1. HEADER (CABEÇALHO)
        // ==============================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .shadow(
                    elevation = 4.dp,
                    shape = RectangleShape,
                    spotColor = Color.Gray,
                    ambientColor = Color.Gray
                )
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
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
                text = "Login",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = CinzaTexto
            )
        }

        // ==============================================================
        // 2. ÁREA SUPERIOR (LOGOS)
        // ==============================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(Color.White)
        ) {
            // Imagens de fundo decorativas
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

            // Logo central e Título
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
        // 3. ÁREA DE FORMULÁRIO
        // ==============================================================
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fundo_ondulado),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 160.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ===== INPUT DE USUÁRIO (EMAIL) =====
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
                            brush = if (isFocusedUsuario) mainGradient else SolidColor(CinzaBorda),
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

                // ===== INPUT SENHA + BOTÃO ENTRAR =====
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
                                brush = if (isFocusedSenha) mainGradient else SolidColor(CinzaBorda),
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

                    // === BOTÃO DE ENTRAR (SETA) ===
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = tamanhoBotao + espacoEntreBotao)
                            .size(tamanhoBotao)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = if (camposValidos) mainGradient else SolidColor(Color(0xFFE0E0E0))
                            )
                            .clickable(enabled = camposValidos && !uiState.isLoading) {
                                // Ação disparada aqui
                                viewModel.login(email, senha)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Entrar",
                                tint = if (camposValidos) Color.White else Color.Gray
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/login/LoginScreen.kt
                            )
                        }
                    }
                }

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/login/LoginScreen.kt
                Spacer(modifier = Modifier.height(20.dp))

                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Logo Pegaí",
                    modifier = Modifier.size(110.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Bem-vindo de volta!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Entre com seus dados para continuar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(40.dp))

                LoginTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "E-mail",
                    placeholder = "exemplo@email.com",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(20.dp))

                LoginTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    label = "Senha",
                    placeholder = "Sua senha secreta",
                    icon = Icons.Default.Lock,
                    isPassword = true
                )

                Text(
                    text = "Esqueceu a senha?",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 12.dp)
                        .clickable { }
                )

                Spacer(modifier = Modifier.height(40.dp))

                // --- Botão Entrar ---
                Button(
                    onClick = {
                        authViewModel.fazerLogin(email, senha) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    },
                    enabled = email.isNotBlank() && senha.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    shape = RoundedCornerShape(16.dp)
=======
                // ===== LINK "Esqueceu a senha?" =====
                Box(modifier = Modifier.width(larguraInputs).padding(top = 8.dp)) {
                    Text(
                        text = "Esqueceu a senha?",
                        fontSize = 12.sp,
                        color = CinzaTexto,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart).clickable {
                            // Ação de esqueci senha aqui
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // ===== BOTÃO CADASTRE-SE =====
                Button(
                    onClick = {
                        navController.navigate("register")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    modifier = Modifier
                        .width(larguraInputs)
                        .height(48.dp),
                    shape = RoundedCornerShape(25.dp)
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/login/LoginScreen.kt
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/login/LoginScreen.kt
                            .background(
                                if (email.isNotBlank() && senha.isNotBlank()) brandGradient()
                                else Brush.linearGradient(listOf(Color.Gray.copy(alpha = 0.4f), Color.Gray.copy(alpha = 0.4f)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Entrar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divisor
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    Text(" ou ", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Image(painter = painterResource(id = R.drawable.google), contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Entrar com Google", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.padding(bottom = 20.dp)) {
                    Text("Não tem uma conta? ", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    Text(
                        text = "Cadastre-se",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navController.navigate("register") }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.Gray) },
            leadingIcon = { Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = getFieldColor(),
                unfocusedContainerColor = getFieldColor(),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            )
        )
    }
=======
                            .background(mainGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cadastre-se",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== BOTÃO LOGIN COM GOOGLE =====
                OutlinedButton(
                    onClick = { /* Lógica do Google Aqui */ },
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

                // Espaço extra no final
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/login/LoginScreen.kt
}