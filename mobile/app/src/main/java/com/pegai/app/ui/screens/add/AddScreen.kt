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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val tabs = listOf("Novo Anúncio", "Meus Produtos")

    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // --- GERENCIAMENTO DE MENSAGENS (SUCESSO OU ERRO) ---
    LaunchedEffect(uiState.mensagemSucesso, uiState.erro) {
        if (uiState.mensagemSucesso != null) {
            // Exibe o Snackbar (serve tanto para Criar quanto para Atualizar)
            snackbarHostState.showSnackbar(uiState.mensagemSucesso!!)

            // Se for criação de novo produto, muda para a aba de lista
            if (uiState.idEmEdicao == null) {
                tabIndex = 1
            }

            viewModel.limparMensagens()
        }
        if (uiState.erro != null) {
            snackbarHostState.showSnackbar(uiState.erro!!)
            viewModel.limparMensagens()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F5F5),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Gerenciar Anúncios", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                }

                TabRow(
                    selectedTabIndex = tabIndex,
                    containerColor = Color.White,
                    contentColor = Color(0xFF0E8FC6),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                            color = Color(0xFF0E8FC6)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title, fontWeight = FontWeight.Bold) },
                            selected = tabIndex == index,
                            onClick = {
                                tabIndex = index
                                if (index == 0) viewModel.limparFormulario()
                            },
                            selectedContentColor = Color(0xFF0E8FC6),
                            unselectedContentColor = Color.Gray
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            if (tabIndex == 0) {
                // ABA 1: NOVO PRODUTO
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(top = 16.dp, bottom = bottomPadding + 80.dp)
                ) {
                    Text("Cadastrar novo item", style = MaterialTheme.typography.titleMedium, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

                    CamposDoFormulario(viewModel, uiState)

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.salvarProduto() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E8FC6))
                    ) {
                        Text("Publicar Agora", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // ABA 2: MEUS PRODUTOS (LISTA)
                ListaMeusProdutos(
                    produtos = uiState.meusProdutos,
                    onManage = { produto -> viewModel.abrirModalEdicao(produto) },
                    bottomPadding = bottomPadding
                )
            }
        }
    }

    // --- MODAL DE EDIÇÃO (BOTTOM SHEET) ---
    if (uiState.mostrarModalEdicao) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.fecharModal() },
            containerColor = Color.White,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    "Editar Produto",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
                )

                CamposDoFormulario(viewModel, uiState)

                Spacer(modifier = Modifier.height(24.dp))

                // Botão Salvar
                Button(
                    onClick = { viewModel.salvarProduto() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E8FC6))
                ) {
                    Text("Salvar Alterações", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botão Excluir
                OutlinedButton(
                    onClick = { viewModel.solicitarExclusao() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.Red),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Excluir Produto")
                }
            }
        }
    }

    // --- DIALOG DE CONFIRMAÇÃO DE EXCLUSÃO ---
    if (uiState.mostrarDialogoExclusao) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelarExclusao() },
            title = { Text(text = "Excluir Produto?") },
            text = { Text("Tem certeza que deseja excluir este item? Essa ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmarExclusao() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Sim, Excluir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.cancelarExclusao() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF0E8FC6))
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = Color.White
        )
    }
}

// --- COMPONENTE REUTILIZÁVEL: CAMPOS DO FORMULÁRIO ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamposDoFormulario(
    viewModel: AddProductViewModel,
    uiState: AddProductUiState
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5),
        onResult = { uris -> viewModel.onFotosSelecionadas(uris) }
    )

    // FOTOS
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .clickable {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (uiState.imagensSelecionadas.isEmpty()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Add, null, tint = Color(0xFF0E8FC6), modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Toque para adicionar fotos", color = Color.Gray)
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.imagensSelecionadas) { imagemUri ->
                    Box {
                        AsyncImage(
                            model = imagemUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(130.dp).clip(RoundedCornerShape(8.dp))
                        )
                        IconButton(
                            onClick = { viewModel.removerFoto(imagemUri) },
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(Color.White, CircleShape)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
    Text("Máximo 5 fotos", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))

    Spacer(modifier = Modifier.height(16.dp))

    // TÍTULO
    OutlinedTextField(
        value = uiState.titulo,
        onValueChange = { viewModel.onTituloChange(it) },
        label = { Text("Título do Anúncio") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF0E8FC6), focusedLabelColor = Color(0xFF0E8FC6))
    )
    Spacer(modifier = Modifier.height(12.dp))

    // PREÇO
    OutlinedTextField(
        value = uiState.preco,
        onValueChange = { viewModel.onPrecoChange(it) },
        label = { Text("Preço por dia (R$)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF0E8FC6), focusedLabelColor = Color(0xFF0E8FC6))
    )
    Spacer(modifier = Modifier.height(12.dp))

    // CATEGORIA
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = uiState.categoria,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoria") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF0E8FC6), focusedLabelColor = Color(0xFF0E8FC6)),
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            viewModel.categoriasDisponiveis.forEach { categoria ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = categoria.icon, contentDescription = null, tint = Color(0xFF0E8FC6), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = categoria.nomeExibicao)
                        }
                    },
                    onClick = {
                        viewModel.onCategoriaChange(categoria.nomeExibicao)
                        expanded = false
                    }
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

    // DESCRIÇÃO
    OutlinedTextField(
        value = uiState.descricao,
        onValueChange = { viewModel.onDescricaoChange(it) },
        label = { Text("Descrição detalhada") },
        modifier = Modifier.fillMaxWidth().height(120.dp),
        shape = RoundedCornerShape(12.dp),
        maxLines = 5,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF0E8FC6), focusedLabelColor = Color(0xFF0E8FC6))
    )
}

@Composable
fun ListaMeusProdutos(
    produtos: List<Product>,
    onManage: (Product) -> Unit,
    bottomPadding: androidx.compose.ui.unit.Dp
) {
    if (produtos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Você ainda não tem anúncios.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = bottomPadding + 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(produtos) { produto ->
                MeuProdutoCard(produto, onManage)
            }
        }
    }
}

@Composable
fun MeuProdutoCard(
    produto: Product,
    onManage: (Product) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable { onManage(produto) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = produto.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(produto.titulo, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("R$ ${produto.preco} / dia", color = Color(0xFF0E8FC6), fontWeight = FontWeight.Bold)
                Text(produto.categoria, fontSize = 12.sp, color = Color.Gray)
            }

            IconButton(onClick = { onManage(produto) }) {
                Icon(Icons.Default.Settings, contentDescription = "Gerenciar", tint = Color.Gray)
            }
        }
    }
}