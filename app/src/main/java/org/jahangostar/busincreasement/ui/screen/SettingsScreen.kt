package org.jahangostar.busincreasement.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component3
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component4
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component5
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component6
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component7
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.jahangostar.busincreasement.data.model.ServerConfig
import org.jahangostar.busincreasement.ui.components.SqlConnectionResponseDialog
import org.jahangostar.busincreasement.ui.theme.BusIncreasementTheme
import org.jahangostar.busincreasement.viewmodel.SettingsViewModel
import org.jahangostar.busincreasement.viewmodel.SqlConnectionViewModel

data class AppSettings(
    var deviceId: String = "",
    var uc: String = "",
    var devicePassword: String = "",
    var serverIp: String = "",
    var serverName: String = "",
    var serverPassword: String = "",
    var organizationName: String = ""
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    sqlViewModel: SqlConnectionViewModel
) {
    var settingsState by remember { mutableStateOf(AppSettings()) }

    var devicePasswordVisible by rememberSaveable { mutableStateOf(false) }
    var serverPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val conext = LocalContext.current
    val device by viewModel.device.collectAsState()
    val config by sqlViewModel.serverConfig.collectAsState()

    LaunchedEffect(config) {
        config?.let {
            settingsState = settingsState.copy(
                serverIp = it.serverIp,
                serverName = it.sqlUser,
                serverPassword = it.sqlPassword
            )
        }
    }

    LaunchedEffect(device) {
        device?.let { fetchedDevice ->
            settingsState = settingsState.copy(
                deviceId = fetchedDevice.deviceId,
                uc = fetchedDevice.uc,
                devicePassword = fetchedDevice.devicePassword,
                organizationName = fetchedDevice.organizationName,
            )
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    val sqlMessage by sqlViewModel.serverMessage.collectAsState()

    LaunchedEffect(sqlMessage) {
        if (sqlMessage != null) {
            showDialog = true
        }
    }

    sqlMessage?.let {
        SqlConnectionResponseDialog(
            showDialog = showDialog,
            onDismissRequest = {
                sqlViewModel.resetServerStatus()
                showDialog = false
            },
            isSuccess = it.contains("ارتباط برقرار است"),
            message = it,
        )
    }


    val (deviceIdFocus, ucFocus, devicePasswordFocus, serverIpFocus, serverNameFocus, serverPasswordFocus, orgNameFocus) = remember { FocusRequester.createRefs() }

    BusIncreasementTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("تنظیمات برنامه") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        keyboardController?.hide()
                        val allFieldsFilled = !settingsState.deviceId.isBlank() &&
                                !settingsState.uc.isBlank() &&
                                !settingsState.devicePassword.isBlank() &&
                                !settingsState.organizationName.isBlank()

                        if (!allFieldsFilled) {
                            Toast.makeText(
                                conext,
                                "لطفا تمامی اطلاعات خواسته شده را وارد نمایید.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.saveDeviceSettings(settingsState)
                            sqlViewModel.insertServerConfig(
                                ServerConfig(
                                    serverIp = settingsState.serverIp,
                                    sqlUser = settingsState.serverName,
                                    sqlPassword = settingsState.serverPassword
                                )
                            )
                        }
                    },
                    icon = { Icon(Icons.Filled.Check, "ذخیره") },
                    text = { Text("ذخیره تنظیمات") }
                )
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Padding from Scaffold
                    .imePadding() // Apply IME padding HERE
                    .padding(horizontal = 16.dp) // Your content padding
                    .verticalScroll(rememberScrollState())
            ) {

                SettingsSectionTitle("اطلاعات دستگاه")

                OutlinedTextField(
                    value = settingsState.deviceId,
                    onValueChange = { settingsState = settingsState.copy(deviceId = it) },
                    label = { Text("شناسه دستگاه") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(deviceIdFocus),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onNext = { ucFocus.requestFocus() })
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = settingsState.uc,
                    onValueChange = { settingsState = settingsState.copy(uc = it) },
                    label = { Text("کد سازمان") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(ucFocus),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onNext = { devicePasswordFocus.requestFocus() })
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = settingsState.organizationName,
                    onValueChange = { settingsState = settingsState.copy(organizationName = it) },
                    label = { Text("نام سازمان") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(orgNameFocus),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus() // Clear focus to hide keyboard
                        keyboardController?.hide()
                    })
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = settingsState.devicePassword,
                    onValueChange = { settingsState = settingsState.copy(devicePassword = it) },
                    label = { Text("رمز عبور دستگاه/برنامه") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(devicePasswordFocus),
                    singleLine = true,
                    visualTransformation = if (devicePasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { serverIpFocus.requestFocus() }),
                    trailingIcon = {
                        val image =
                            if (devicePasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description =
                            if (devicePasswordVisible) "عدم نمایش رمز" else "نمایش رمز"
                        IconButton(onClick = { devicePasswordVisible = !devicePasswordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )

                SettingsSectionTitle("تنظیمات اتصال به سرور")

                OutlinedTextField(
                    value = settingsState.serverIp,
                    onValueChange = { settingsState = settingsState.copy(serverIp = it) },
                    label = { Text("آدرس آیپی سرور") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(serverIpFocus),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(onNext = { serverNameFocus.requestFocus() }),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = settingsState.serverName,
                    onValueChange = { settingsState = settingsState.copy(serverName = it) },
                    label = { Text("نام سرور") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(serverNameFocus),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { serverPasswordFocus.requestFocus() })
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = settingsState.serverPassword,
                    onValueChange = { settingsState = settingsState.copy(serverPassword = it) },
                    label = { Text("رمز عبور سرور") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(serverPasswordFocus),
                    singleLine = true,
                    visualTransformation = if (serverPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { orgNameFocus.requestFocus() }),
                    trailingIcon = {
                        val image =
                            if (serverPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description =
                            if (serverPasswordVisible) "عدم نمایش رمز" else "نمایش رمز"
                        IconButton(onClick = { serverPasswordVisible = !serverPasswordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(80.dp)) // Space for the FAB
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        textAlign = TextAlign.Start
    )
}
