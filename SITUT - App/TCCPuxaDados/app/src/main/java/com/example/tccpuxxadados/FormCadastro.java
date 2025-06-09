package com.example.tccpuxxadados;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class FormCadastro extends AppCompatActivity {

    private EditText edit_nome, edit_email, edit_senha;
    private Button btn_cad;
    private FirebaseAuth mAuth;
    String[] mensagem = {"Preencha Todos os Campos", "Cadastro Realizado com Sucesso", "Verifique seu Email"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        mAuth = FirebaseAuth.getInstance();
        IniciarComponentes();

        // Botão de cadastro
        btn_cad.setOnClickListener(v -> {
            String nome = edit_nome.getText().toString();
            String email = edit_email.getText().toString();
            String senha = edit_senha.getText().toString();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Snackbar snackbar = Snackbar.make(v, mensagem[0], Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            } else {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    CadastrarUsuario(v);
                    startActivity(new Intent(FormCadastro.this, FormLogin.class));
                    finish();
                }, 2000);
            }
        });

        // Clique no "Esqueceu a senha?"
        findViewById(R.id.esqueceu_senha).setOnClickListener(v -> {
            String email = edit_email.getText().toString().trim();
            if (email.isEmpty()) {
                Snackbar snackbar = Snackbar.make(v, "Informe seu e-mail para recuperação de senha.", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            } else {
                enviarEmailRecuperacao(email);
            }
        });
    }

    private void CadastrarUsuario(View v) {
        String nome = edit_nome.getText().toString();
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        String allowedDomain = "@gmail.com"; // Domínio permitido

        if (!email.endsWith(allowedDomain)) {
            Snackbar.make(v, "Apenas e-mails " + allowedDomain + " são permitidos!", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .setTextColor(Color.WHITE)
                    .show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nome)
                                    .build();

                            user.updateProfile(profileUpdates);

                            user.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                                if (verifyTask.isSuccessful()) {
                                    Snackbar.make(v, mensagem[2], Snackbar.LENGTH_LONG)
                                            .setBackgroundTint(Color.GREEN)
                                            .setTextColor(Color.WHITE)
                                            .show();

                                    // Informar o usuário que ele precisa verificar o email
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> verificarEmail(v), 2000);
                                } else {
                                    Snackbar.make(v, "Falha ao enviar email de verificação.", Snackbar.LENGTH_SHORT)
                                            .setBackgroundTint(Color.RED)
                                            .setTextColor(Color.WHITE)
                                            .show();
                                }
                            });
                        }
                    } else {
                        tratarErroCadastro(task.getException(), v);
                    }
                });
    }


    private void enviarEmailRecuperacao(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(FormCadastro.this, "E-mail de recuperação enviado!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FormCadastro.this, "Erro ao enviar e-mail de recuperação: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void SalvarDadosUsuario() {
        String nome = edit_nome.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("nome", nome);
        String usuarioID = mAuth.getCurrentUser().getUid();
        db.collection("Usuarios").document(usuarioID).set(usuarios)
                .addOnSuccessListener(unused -> Log.d("db", "Sucesso ao Salvar os Dados"))
                .addOnFailureListener(e -> Log.d("db_error", "Erro ao Salvar os Dados" + e.toString()));
    }

    private void verificarEmail(View v) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.reload().addOnCompleteListener(task -> { // Atualiza o estado do usuário
                if (task.isSuccessful() && user.isEmailVerified()) {
                    SalvarDadosUsuario();
                    Snackbar.make(v, mensagem[1], Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.GREEN)
                            .setTextColor(Color.WHITE)
                            .show();

                    startActivity(new Intent(FormCadastro.this, FormLogin.class));
                    finish();
                } else {
                    Snackbar.make(v, "Confirme seu email antes de continuar.", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.RED)
                            .setTextColor(Color.WHITE)
                            .show();
                }
            });
        }
    }


    private void tratarErroCadastro(Exception exception, View v) {
        String erro;
        try {
            throw exception;
        } catch (FirebaseAuthWeakPasswordException e) {
            erro = "A senha deve ter no mínimo 6 caracteres";
        } catch (FirebaseAuthUserCollisionException e) {
            erro = "Email já Cadastrado";
        } catch (FirebaseAuthInvalidCredentialsException e) {
            erro = "Email Inválido";
        } catch (Exception e) {
            erro = "Erro ao Cadastrar Usuário";
        }
        Snackbar.make(v, erro, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.WHITE)
                .setTextColor(Color.BLACK).show();
    }

    private void IniciarComponentes() {
        edit_nome = findViewById(R.id.editName);
        edit_email = findViewById(R.id.editEmail);
        edit_senha = findViewById(R.id.editSenha);
        btn_cad = findViewById(R.id.btnCadastrar);
    }
}
