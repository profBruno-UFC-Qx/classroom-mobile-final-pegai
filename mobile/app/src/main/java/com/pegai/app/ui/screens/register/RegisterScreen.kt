package com.pegai.app.ui.screens.register

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
import com.pegai.app.ui.viewmodel.register.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var nome by remember { mutableStateOf("") }
    var sobrenome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

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
                }

                Spacer(modifier = Modifier.height(40.dp))

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
            }
        }
    }
}

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
            )
        )
    }
}