package com.example.lighthouseofmemory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button logoutButton;
    private Button connectButton;
    private Button deleteButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings); // XML 파일의 이름을 확인하세요.

        // UI 요소 초기화
        logoutButton = findViewById(R.id.logout_button);
        backButton = findViewById(R.id.Back_b);
        
        deleteButton = findViewById(R.id.delete_account);


        // 뒤로가기 버튼 클릭 이벤트
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 현재 액티비티 종료
            }
        });


        // 로그아웃 버튼 클릭 이벤트
        logoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setMessage("로그아웃 하시겠습니까?");
                    builder.setTitle("로그아웃");
                    builder.setCancelable(false);
                    builder.setPositiveButton("예", (DialogInterface.OnClickListener) (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(SettingActivity.this, "로그아웃 완료.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
                        finish();
                    });
                    builder.setNegativeButton("아니오", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(SettingActivity.this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

                // Create the object of AlertDialog Builder class



        });



        // 탈퇴하기 버튼 클릭 이벤트


        deleteButton.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String email = user.getEmail();
                showPasswordDialog(email);
            } else {
                Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });



    }
    private void showPasswordDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("계정 삭제를 위해 비밀번호를 다시 입력하세요");


        final EditText passwordInput = new EditText(this);
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordInput);

        passwordInput.setTextColor(Color.rgb(74,68,89)); // Change to your desired color
        passwordInput.setHintTextColor(Color.GRAY); // Change to your desired color for hint
        passwordInput.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(247,242,250)));


        builder.setPositiveButton("확인", (dialog, which) -> {
            String password = passwordInput.getText().toString().trim();
            reauthenticateAndDelete(email, password);
        });
        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void reauthenticateAndDelete(String email, String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Proceed to delete the account
                    user.delete().addOnCompleteListener(deleteTask -> {
                        if (deleteTask.isSuccessful()) {
                            Toast.makeText(this, "계정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "계정 삭제 오류", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
