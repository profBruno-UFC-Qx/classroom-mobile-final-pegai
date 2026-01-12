package com.pegai.app.ui.screens.add

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.pegai.app.model.Product
import com.pegai.app.ui.components.GuestPlaceholder
import com.pegai.app.ui.theme.brandGradient
import com.pegai.app.ui.theme.getFieldColor
import com.pegai.app.ui.viewmodel.AuthViewModel
import com.pegai.app.ui.viewmodel.add.AddProductUiState
import com.pegai.app.ui.viewmodel.add.AddProductViewModel

@Composable
fun AddScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: AddProductViewModel = viewModel()
) {
    val user by authViewModel.usuarioLogado.collectAsState()

    LaunchedEffect(user) {
        user?.let { usuario ->
            viewModel.carregarMeusProdutos(usuario.uid)
        }
    }

    if (user == null) {
        GuestPlaceholder(
            title = "Anuncie seu Produto",
            subtitle = "Faça login para criar anúncios e começar a ganhar dinheiro alugando seus itens.",
            onLoginClick = { navController.navigate("login") },
            onRegisterClick = { navController.navigate("register") }
        )
    } else {
        AddProductContent(viewModel, navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductContent(
    viewModel: AddProductViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var tabIndex by remember { mutableIntStateOf(0) }

    val dynamicGradient = brandGradient()
    val tabs = listOf(
        "Novo Anúncio" to Icons.Default.AddCircleOutline,
        "Meus Produtos" to Icons.Default.Inventory2
    )

    LaunchedEffect(uiState.mensagemSucesso, uiState.erro) {
        if (uiState.mensagemSucesso != null) {
            snackbarHostState.showSnackbar(uiState.mensagemSucesso!!)
            if (uiState.idEmEdicao == null) tabIndex = 1
            viewModel.limparMensagens()
        }
        if (uiState.erro != null) {
            snackbarHostState.showSnackbar(uiState.erro!!)
            viewModel.limparMensagens()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            Column(modifier = Modifier.fillMaxWidth().background(dynamicGradient)) {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                    Text("Gerenciar Anúncios", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    TabRow(
                        selectedTabIndex = tabIndex,
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            Box(Modifier.tabIndicatorOffset(tabPositions[tabIndex]).height(3.dp).padding(horizontal = 32.dp).background(dynamicGradient, CircleShape))
                        },
                        divider = {},
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        tabs.forEachIndexed { index, (title, icon) ->
                            val isSelected = tabIndex == index
                            val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

                            Tab(
                                selected = isSelected,
                                onClick = {
                                    tabIndex = index
                                    if (index == 0) viewModel.limparFormulario()
                                },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = contentColor)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 14.sp, color = contentColor)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (tabIndex == 0) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(rememberScrollState()).padding(top = 16.dp, bottom = 100.dp)
                    ) {
                        Text("Detalhes do Item", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))

                        CamposDoFormulario(viewModel, uiState)

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { viewModel.salvarProduto() },
                            modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Box(modifier = Modifier.fillMaxSize().background(dynamicGradient), contentAlignment = Alignment.Center) {
                                if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                else Text("Publicar Anúncio", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }
                } else {
                    ListaMeusProdutos(
                        produtos = uiState.meusProdutos,
                        onManage = { produto -> viewModel.abrirModalEdicao(produto) },
                        bottomPadding = 80.dp
                    )
                }
            }
        }
    }

    if (uiState.mostrarModalEdicao) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.fecharModal() },
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)) }
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).verticalScroll(rememberScrollState()).padding(bottom = 40.dp)) {
                Text("Editar Produto", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 24.dp))
                CamposDoFormulario(viewModel, uiState)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.salvarProduto() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Salvar Alterações", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { viewModel.solicitarExclusao() }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Excluir Anúncio", color = Color.Red, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    if (uiState.mostrarDialogoExclusao) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelarExclusao() },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Excluir Anúncio?", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("Essa ação removerá o produto permanentemente da plataforma.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)) },
            confirmButton = { Button(onClick = { viewModel.confirmarExclusao() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Excluir") } },
            dismissButton = { TextButton(onClick = { viewModel.cancelarExclusao() }) { Text("Voltar", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamposDoFormulario(viewModel: AddProductViewModel, uiState: AddProductUiState) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
        onResult = { uris -> viewModel.onFotosSelecionadas(uris) }
    )

    val currentFieldColor = getFieldColor()

    Box(
        modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(20.dp)).background(currentFieldColor).border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(20.dp)).clickable {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        contentAlignment = Alignment.Center
    ) {
        if (uiState.imagensSelecionadas.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
                    Icon(Icons.Default.AddPhotoAlternate, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(12.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Adicionar fotos do item", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                Text("Até 5 fotos em alta qualidade", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 12.sp)
            }
        } else {
            LazyRow(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.imagensSelecionadas) { uri ->
                    Box {
                        AsyncImage(model = uri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(136.dp).clip(RoundedCornerShape(12.dp)))
                        Surface(
                            onClick = { viewModel.removerFoto(uri) },
                            modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(24.dp),
                            shape = CircleShape, color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    PremiumTextField(value = uiState.titulo, onValueChange = { viewModel.onTituloChange(it) }, label = "Título do anúncio", placeholder = "Ex: Câmera Canon T7i", icon = Icons.Default.Title)

    Spacer(modifier = Modifier.height(16.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            PremiumTextField(value = uiState.preco, onValueChange = { viewModel.onPrecoChange(it) }, label = "Preço/Dia", placeholder = "0,00", icon = Icons.Default.Payments, keyboardType = KeyboardType.Number)
        }

        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.weight(1.2f)) {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                PremiumTextField(value = uiState.categoria, onValueChange = {}, label = "Categoria", placeholder = "Selecionar", icon = Icons.Default.Category, readOnly = true, modifier = Modifier.menuAnchor(), trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) })
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))) {
                    viewModel.categoriasDisponiveis.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nomeExibicao, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface) },
                            leadingIcon = { Icon(cat.icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) },
                            onClick = { viewModel.onCategoriaChange(cat.nomeExibicao); expanded = false }
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    PremiumTextField(value = uiState.descricao, onValueChange = { viewModel.onDescricaoChange(it) }, label = "Descrição", placeholder = "Conte detalhes sobre o estado do item...", icon = Icons.Default.Description, singleLine = false, modifier = Modifier.height(120.dp))
}

@Composable
fun PremiumTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(bottom = 6.dp, start = 4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.Gray) },
            leadingIcon = { Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
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
}

@Composable
fun ListaMeusProdutos(produtos: List<Product>, onManage: (Product) -> Unit, bottomPadding: androidx.compose.ui.unit.Dp) {
    if (produtos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(bottom = 100.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Inventory, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), modifier = Modifier.size(100.dp))
                Text("Nenhum anúncio ativo", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(produtos) { produto -> MeuProdutoCard(produto, onManage) }
            item { Spacer(modifier = Modifier.height(bottomPadding)) }
        }
    }
}

@Composable
fun MeuProdutoCard(produto: Product, onManage: (Product) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onManage(produto) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = produto.imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(getFieldColor()))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(produto.titulo, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                Text("R$ ${produto.preco}/dia", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Surface(color = getFieldColor(), shape = RoundedCornerShape(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                    Text(produto.categoria, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            IconButton(onClick = { onManage(produto) }, modifier = Modifier.background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), CircleShape)) {
                Icon(Icons.Default.Settings, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), modifier = Modifier.size(20.dp))
            }
        }
    }
}