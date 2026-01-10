package com.pegai.app.ui.screens.register

import android.widget.Toast
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
=======
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
import androidx.compose.material.icons.filled.*
=======
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
=======
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
=======
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pegai.app.R
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
import com.pegai.app.ui.viewmodel.register.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
=======
    LaunchedEffect(uiState) {
        if (uiState.erro != null) {
            Toast.makeText(context, uiState.erro, Toast.LENGTH_LONG).show()
        }
        if (uiState.cadastroSucesso) {
            Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
            navController.navigate(route = "home")
        }
    }

    val VerdePrincipal = Color(0xFF88D888)
    val CinzaBorda = Color(0xFFD3D3D3)
    val CinzaTexto = Color(0xFF5A5A5A)
    val CinzaInputBackground = Color(0xFFF5F5F5)
    val gradienteBotao = Brush.linearGradient(
        colors = listOf(Color(0xFFAAD87A), Color(0xFF88D888), Color(0xFF66E0B8)),
        start = Offset.Zero, end = Offset.Infinite
    )
    val corDesabilitado = Color(0xFFBFC4CE)
    val larguraInputs = 350.dp
    val alturaComponente = 60.dp

>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
    var nome by remember { mutableStateOf("") }
    var sobrenome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
    val camposValidos = nome.isNotBlank() && sobrenome.isNotBlank() &&
            email.isNotBlank() && telefone.isNotBlank() &&
            senha.length >= 6 && senha == confirmarSenha

    LaunchedEffect(uiState) {
        if (uiState.erro != null) {
            Toast.makeText(context, uiState.erro, Toast.LENGTH_LONG).show()
        }
        if (uiState.cadastroSucesso) {
            Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
            navController.navigate("home") {
                popUpTo("register") { inclusive = true }
            }
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
                        modifier = Modifier.size(48.dp).shadow(4.dp, CircleShape),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Criar Perfil",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = "Preencha os dados abaixo para começar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- Campos de Nome e Sobrenome Lado a Lado ---
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        RegisterTextField(value = nome, onValueChange = { nome = it }, label = "Nome", placeholder = "João", icon = Icons.Default.Person)
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        RegisterTextField(value = sobrenome, onValueChange = { sobrenome = it }, label = "Sobrenome", placeholder = "Silva", icon = Icons.Default.Badge)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                RegisterTextField(value = email, onValueChange = { email = it }, label = "E-mail", placeholder = "joao@email.com", icon = Icons.Default.Email, keyboardType = KeyboardType.Email)

                Spacer(modifier = Modifier.height(16.dp))

                RegisterTextField(value = telefone, onValueChange = { telefone = it }, label = "Telefone", placeholder = "(88) 99999-9999", icon = Icons.Default.Phone, keyboardType = KeyboardType.Phone)

                Spacer(modifier = Modifier.height(16.dp))

                RegisterTextField(value = senha, onValueChange = { senha = it }, label = "Senha", placeholder = "Mínimo 6 caracteres", icon = Icons.Default.Lock, isPassword = true)

                Spacer(modifier = Modifier.height(16.dp))

                RegisterTextField(value = confirmarSenha, onValueChange = { confirmarSenha = it }, label = "Confirmar Senha", placeholder = "Repita sua senha", icon = Icons.Default.LockClock, isPassword = true, imeAction = ImeAction.Done)

                if (senha.isNotEmpty() && confirmarSenha.isNotEmpty() && senha != confirmarSenha) {
                    Text(
                        text = "As senhas não coincidem",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)
                    )
=======
    var isFocusedNome by remember { mutableStateOf(false) }
    var isFocusedSNome by remember { mutableStateOf(false) }
    var isFocusedEmail by remember { mutableStateOf(false) }
    var isFocusedTel by remember { mutableStateOf(false) }
    var isFocusedSenha by remember { mutableStateOf(false) }
    var isFocusedConfSenha by remember { mutableStateOf(false) }

    val camposValidos = nome.isNotBlank() &&
            sobrenome.isNotBlank() &&
            email.isNotBlank() &&
            telefone.isNotBlank() &&
            senha.length >= 6 &&
            senha == confirmarSenha

    Box(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.blobs1), contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.width(180.dp).height(180.dp).align(Alignment.TopStart).offset(y = 30.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.blobs2), contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.width(110.dp).height(110.dp).align(Alignment.BottomEnd).offset(y = 25.dp)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    .shadow(elevation = 4.dp, shape = RectangleShape)
                    .background(Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = CinzaTexto)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Criar Conta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CinzaTexto)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    modifier = Modifier.width(larguraInputs).padding(bottom = 30.dp, top = 20.dp)
                ) {
                    Text("    Vamos começar,", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("           Criando seu perfil!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                // --- Campo Nome ---
                InputItem(
                    modifier = Modifier.width(larguraInputs),
                    label = "Nome",
                    value = nome,
                    onValueChange = { nome = it },
                    placeholder = "Ex: João",
                    isFocused = isFocusedNome,
                    onFocusChange = { isFocusedNome = it },
                    verdePrincipal = VerdePrincipal, cinzaBorda = CinzaBorda, cinzaBg = CinzaInputBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- Campo Sobrenome ---
                InputItem(
                    modifier = Modifier.width(larguraInputs),
                    label = "Sobrenome",
                    value = sobrenome,
                    onValueChange = { sobrenome = it },
                    placeholder = "Ex: Silva",
                    isFocused = isFocusedSNome,
                    onFocusChange = { isFocusedSNome = it },
                    verdePrincipal = VerdePrincipal, cinzaBorda = CinzaBorda, cinzaBg = CinzaInputBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- Campo Email ---
                InputItem(
                    modifier = Modifier.width(larguraInputs),
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Ex: exemplo@email.com",
                    keyboardType = KeyboardType.Email,
                    isFocused = isFocusedEmail,
                    onFocusChange = { isFocusedEmail = it },
                    verdePrincipal = VerdePrincipal, cinzaBorda = CinzaBorda, cinzaBg = CinzaInputBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- Campo Telefone ---
                InputItem(
                    modifier = Modifier.width(larguraInputs),
                    label = "Telefone",
                    value = telefone,
                    onValueChange = { telefone = it },
                    placeholder = "Ex: (88) 99999-9999",
                    keyboardType = KeyboardType.Phone,
                    isFocused = isFocusedTel,
                    onFocusChange = { isFocusedTel = it },
                    verdePrincipal = VerdePrincipal, cinzaBorda = CinzaBorda, cinzaBg = CinzaInputBackground
                )
                Spacer(modifier = Modifier.height(16.dp))

                //  --- LINHA DE SENHAS (Lado a Lado) ---
                Row(
                    modifier = Modifier.width(larguraInputs),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Senha
                    InputItem(
                        modifier = Modifier.weight(1f),
                        label = "Senha",
                        value = senha,
                        onValueChange = { senha = it },
                        placeholder = "******",
                        isPassword = true,
                        isFocused = isFocusedSenha,
                        onFocusChange = { isFocusedSenha = it },
                        verdePrincipal = VerdePrincipal, cinzaBorda = CinzaBorda, cinzaBg = CinzaInputBackground
                    )

                    Spacer(modifier = Modifier.width(12.dp)) // Espacinho entre os dois campos

                    // confirmação de senha
                    InputItem(
                        modifier = Modifier.weight(1f),
                        label = "Confirmar Senha",
                        value = confirmarSenha,
                        onValueChange = { confirmarSenha = it },
                        placeholder = "******",
                        isPassword = true,
                        imeAction = ImeAction.Done,
                        isFocused = isFocusedConfSenha,
                        onFocusChange = { isFocusedConfSenha = it },
                        verdePrincipal = VerdePrincipal, cinzaBorda = CinzaBorda, cinzaBg = CinzaInputBackground
                    )
                }

                if (senha.isNotEmpty() && confirmarSenha.isNotEmpty() && senha != confirmarSenha) {
                    Text("As senhas não coincidem", color = Color.Red, fontSize = 15.sp, modifier = Modifier.padding(top = 4.dp))
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
                }

                Spacer(modifier = Modifier.height(40.dp))

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
                // Botão Criar Conta
                Button(
                    onClick = { viewModel.cadastrarUsuario(nome, sobrenome, email, senha, telefone) },
                    enabled = camposValidos && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(
                            if (camposValidos) brandGradient()
                            else Brush.linearGradient(listOf(Color.Gray.copy(alpha = 0.4f), Color.Gray.copy(alpha = 0.4f)))
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Cadastrar Agora", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.padding(bottom = 40.dp)) {
                    Text("Já tem uma conta? ", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    Text(
                        text = "Entrar",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                }
=======
                Box(
                    modifier = Modifier
                        .width(larguraInputs)
                        .height(48.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(
                            if (camposValidos) gradienteBotao else SolidColor(corDesabilitado)
                        )
                        .clickable(enabled = camposValidos && !uiState.isLoading) {
                            viewModel.cadastrarUsuario(
                                nome = nome,
                                sobrenome = sobrenome,
                                email = email,
                                senha = senha,
                                telefone = telefone,
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Criar Conta",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
            }
        }
    }
}
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt

@Composable
fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
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
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = getFieldColor(),
                unfocusedContainerColor = getFieldColor(),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
=======
@Composable
fun InputItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    verdePrincipal: Color,
    cinzaBorda: Color,
    cinzaBg: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isPassword: Boolean = false
) {
    val CinzaTexto = Color(0xFF5A5A5A)
    val alturaComponente = 60.dp

    Column(modifier = modifier) {
        Text(label, color = CinzaTexto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            modifier = Modifier
                .fillMaxWidth()
                .height(alturaComponente)
                .onFocusChanged { onFocusChange(it.isFocused) }
                .border(2.dp, if (isFocused) verdePrincipal else cinzaBorda, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = cinzaBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/screens/register/RegisterScreen.kt
            )
        )
    }
}