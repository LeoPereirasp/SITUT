package com.example.tccpuxxadados;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class FormLogin extends AppCompatActivity {

    private TextView cadastrar;
    private Button btnEntrar;
    private ImageButton btnLoginGoogle;
    private EditText editEmail, editSenha;
    private FirebaseAuth mAuth;  // Instância do FirebaseAuth
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;  // Código de requisição para login com Google

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logar_google);  // Carrega o layout que você forneceu

        // Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Configurar o Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Substitua com seu Client ID do Firebase
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Associar as variáveis aos campos TextInputEditText do XML
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        cadastrar = findViewById(R.id.Cadastro);

        // Botão de login com e-mail e senha
        btnEntrar = findViewById(R.id.btnEntrar);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithEmail();  // Chama o método para realizar o login com e-mail e senha
            }
        });

        // Listener para abrir a tela de cadastro
        cadastrar.setOnClickListener(view -> {
            Intent intent = new Intent(FormLogin.this, FormCadastro.class);
            startActivity(intent);
        });

        // Botão de login do Google
        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();  // Método para realizar login com Google
            }
        });
    }

    // Método para login com e-mail e senha
    private void loginWithEmail() {
        String email = editEmail.getText().toString().trim();  // Pega o texto do campo de e-mail
        String senha = editSenha.getText().toString().trim();  // Pega o texto do campo de senha

        // Verificar se os campos não estão vazios
        if (email.isEmpty()) {
            editEmail.setError("Email é obrigatório!");  // Exibe um erro no campo de e-mail
            return;
        }
        if (senha.isEmpty()) {
            editSenha.setError("Senha é obrigatória!");  // Exibe um erro no campo de senha
            return;
        }

        // Verificar se o e-mail possui o domínio permitido
        String allowedDomain = "@gmail.com";  // Substitua pelo domínio desejado
        if (!email.endsWith(allowedDomain)) {
            editEmail.setError("Apenas e-mails " + allowedDomain + " são permitidos!");  // Exibe um erro no campo de e-mail
            return;
        }

        // Tenta autenticar com Firebase usando e-mail e senha
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login bem-sucedido
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null && user.isEmailVerified()) {
                            Toast.makeText(FormLogin.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

                            // Redirecionar para a HomeActivity
                            startActivity(new Intent(FormLogin.this, primeiro_acesso.class));

                            // Finaliza a FormLogin para evitar que o usuário retorne para a tela de login
                            finish();
                        } else {
                            // Se o email não estiver verificado, exibe mensagem de erro
                            Toast.makeText(FormLogin.this, "Confirme seu email antes de continuar.", Toast.LENGTH_LONG).show();

                            // Opcional: desloga o usuário para evitar inconsistências
                            mAuth.signOut();
                        }
                    } else {
                        // Falhou, exibir erro
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido";
                        Toast.makeText(FormLogin.this, "Falha ao fazer login: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if(usuarioAtual != null) {
            TelaPrincipal();
        }
    }

    private void TelaPrincipal(){
        Intent intent = new Intent(FormLogin.this,Form_home.class);
        startActivity(intent);
    }

    // Método de login com Google
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);  // Inicia o fluxo de login com Google
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verificar se o requestCode é o correto para o login do Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // Lidar com o resultado do login do Google
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            // Se o login do Google foi bem-sucedido
            GoogleSignInAccount account = completedTask.getResult();
            firebaseAuthWithGoogle(account);
        } catch (Exception e) {
            // Se falhou, exibe o erro detalhado
            String errorMessage = e.getMessage() != null ? e.getMessage() : "Erro desconhecido";
            Toast.makeText(this, "Erro ao autenticar com Google: " + errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    // Autenticar no Firebase com a conta do Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        // Realiza o login no Firebase
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login bem-sucedido
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Salvar nome e e-mail no Firebase Database
                            salvarDadosUsuario(user.getDisplayName(), user.getEmail());
                        }

                        Toast.makeText(FormLogin.this, "Login com Google bem-sucedido!", Toast.LENGTH_SHORT).show();

                        // Redirecionar para a HomeActivity
                        startActivity(new Intent(FormLogin.this, primeiro_acesso.class));

                        // Finaliza a FormLogin para evitar que o usuário retorne para a tela de login
                        finish();
                    } else {
                        // Falha no login
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Erro desconhecido";
                        Toast.makeText(FormLogin.this, "Falha ao fazer login com Google: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para salvar o nome e e-mail do usuário no Firebase Realtime Database
    private void salvarDadosUsuario(String nome, String email) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        String userId = mAuth.getCurrentUser().getUid();

        // Criar um mapa com os dados a serem salvos
        Map<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nome", nome);
        dadosUsuario.put("email", email);

        // Salvar os dados no Firebase Realtime Database sob o UID do usuário
        mDatabase.child(userId).setValue(dadosUsuario)
                .addOnSuccessListener(aVoid -> Toast.makeText(FormLogin.this, "Dados do usuário salvos!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(FormLogin.this, "Erro ao salvar dados do usuário.", Toast.LENGTH_SHORT).show());
    }
}
