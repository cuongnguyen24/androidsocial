package com.utt.tt21.cc_modulelogin;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.utt.tt21.cc_modulelogin.authentication.SignInActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.utt.tt21.cc_modulelogin.adapter.ViewPager2Adapter;
import com.utt.tt21.cc_modulelogin.home.homeAdapter.ImageAdapter;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.time.LocalDate;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private Button btnLogout;
    private Button btnPostUp;
    private TableLayout tableLayout;
    private ViewPager2 viewPager2;
    private BottomNavigationView bottomNavigationView;
    private LinearLayout bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private int idFragment;
    private Toolbar toolbar;
    private Button btnCancel;
    private EditText tvDes;
    private Random random = new Random();
    //Add quang kuns
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private List<Uri> imageUris = new ArrayList<>(); // Khai báo danh sách chứa URIs
    private TextView tv_nickname;
    private EditText edtContent;
    private Button btnUpStatus;
    private ImageView imvAvatar;
    private ImageView imgStatus;
    private ImageView chooseImage,takeAPicture;
    private RecyclerView recyclerViewImage;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        initId();
        ViewPager2Adapter adapter = new ViewPager2Adapter(this);
        viewPager2.setAdapter(adapter);
        ChangePage();
        viewPager2.setOffscreenPageLimit(1);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                setBtnCancel();
            }
        });

        btnUpStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPushDataFromEditText();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });

        loadUserAvatar();
        readNicknameDatabase();
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();

            }
        });
        takeAPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();// Gọi hàm mở camera

            }
        });
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(this, imageUris);
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
    }

    private void readNicknameDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("QuangTest/nickname");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                tv_nickname.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //push data
    private void onClickPushDataFromEditText() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser(); // Lấy người dùng hiện tại

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Lấy userId từ người dùng hiện tại
            String content = edtContent.getText().toString(); // Lấy nội dung từ EditText

            if (!content.isEmpty()) {
                // Gọi hàm push dữ liệu
                pushDataToFirebase(content);
            } else {
                // Xử lý lỗi nếu nội dung rỗng
                Log.e("PushData", "Content is empty");
            }
        } else {
            // Xử lý lỗi nếu người dùng chưa đăng nhập
            Log.e("PushData", "User not logged in");
        }
    }

    private void pushDataToFirebase( String edtContent) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // Lấy user hiện tại từ Firebase Auth
        if (user == null) {
            Log.e("PushData", "User is not logged in.");
            return; // Nếu không có người dùng nào đang đăng nhập, không thực hiện hành động
        }

        String userid = user.getUid(); // Lấy userId
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("list_status");;

        // Kiểm tra xem node của userId đã tồn tại hay chưa
        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int index = 1; // Bắt đầu từ 1
                String idStatus;

                // Tìm key tiếp theo cho idStatus
                while (snapshot.hasChild(userid + "_" + index)) {
                    index++;
                }
                idStatus = userid + "_" + index; // Tạo idStatus mới
                // Đẩy dữ liệu vào node con mới
                reference.child(userid).child(idStatus).child("content").setValue(edtContent);
                reference.child(userid).child(idStatus).child("timestamp").setValue(random.nextInt(25) +"h");
                reference.child(userid).child(idStatus).child("commentCount").setValue(0);
                reference.child(userid).child(idStatus).child("likeCount").setValue(0);
                reference.child(userid).child(idStatus).child("postCount").setValue(0);
                reference.child(userid).child(idStatus).child("reupCount").setValue(0);
                reference.child(userid).child(idStatus).child("uid").setValue(index);
                uploadImageToStorage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
                Log.e("PushData", "Database error: " + error.getMessage());
            }
        });
    }

    private void readDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("list_status");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Duyệt qua các phần tử trong list_status
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    // Duyệt qua các trạng thái của từng user
                    for (DataSnapshot statusSnapshot : userSnapshot.getChildren()) {
                        // Lấy userid từ QuangTest
                        String userId = userSnapshot.getKey(); // Lấy key của user, tương ứng với userid
                        // Nếu bạn muốn lấy nội dung khác từ statusSnapshot
                        String content = statusSnapshot.child("content").getValue(String.class);

                        // Hiển thị userid và content
                        tv_nickname.setText("UserID: " + userId + "\nContent: " + content);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void onClickGetData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("quang/name");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);

                tv_nickname.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setCheckedIcon(int idFragment) {
        switch (idFragment)
        {
            case 0:
                bottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);

                break;
            case 1:
                bottomNavigationView.getMenu().findItem(R.id.action_search).setChecked(true);


                break;
            case 3:
                bottomNavigationView.getMenu().findItem(R.id.action_notification).setChecked(true);


                break;
            case 4:
                bottomNavigationView.getMenu().findItem(R.id.action_profile).setChecked(true);


                break;

        }
    }

    private void ChangePage() {
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position)
                {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
                        idFragment = 0;
                        //reloadHome();
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.action_search).setChecked(true);
                        idFragment = 1;

                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.action_notification).setChecked(true);
                        idFragment = 3;

                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.action_profile).setChecked(true);
                        idFragment = 4;

                        break;

                }
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.action_home)
                {
                    viewPager2.setCurrentItem(0, false);
                    //reloadHome();
                } else if (item.getItemId() == R.id.action_search) {
                    viewPager2.setCurrentItem(1, false);
                }
                else if (item.getItemId() == R.id.action_add) {
                    if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    }
                    else
                    {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        setBtnCancel();
                    }
                }
                else if (item.getItemId() == R.id.action_notification)
                {
                    viewPager2.setCurrentItem(3, false);
                }
                else if (item.getItemId() == R.id.action_profile)
                {
                    viewPager2.setCurrentItem(4, false);
                }
                return true;
            }
        });
    }

    private void reloadHome() {
        Intent intent = getIntent();
        finish(); // Kết thúc Activity hiện tại
        startActivity(intent); // Khởi động lại Activity
    }

    private void loadUserAvatar() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            // Tham chiếu đến ảnh avatar trong Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("users/").child(userId).child(userId + ".jpg");

            // Sử dụng Picasso để tải ảnh từ Storage và hiển thị vào ImageView
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Dùng Picasso để load ảnh vào ImageView
                Picasso.get().load(uri).into(imvAvatar);
            }).addOnFailureListener(exception -> {
                Log.e("FirebaseStorage", "Error loading image", exception);
            });
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Cho phép chọn nhiều hình ảnh
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), PICK_IMAGE_REQUEST);
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == MainActivity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) { // Nếu chọn nhiều ảnh
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri); // Thêm URI vào danh sách
                    }
                } else if (data.getData() != null) { // Nếu chỉ chọn 1 ảnh
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri); // Thêm URI vào danh sách
                }
                // Cập nhật RecyclerView sau khi thêm hình ảnh
                imageAdapter.notifyDataSetChanged(); // Cập nhật lại dữ liệu cho RecyclerView
            }
        }
        // Xử lý ảnh chụp từ camera
        if (requestCode == CAMERA_REQUEST && resultCode == MainActivity.RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");  // Ảnh dạng Bitmap
            // Chuyển Bitmap thành URI hoặc lưu lại để dùng
            Uri imageUri = getImageUri(photo);  // Hàm getImageUri để chuyển Bitmap thành Uri
            imageUris.add(imageUri);  // Thêm URI của ảnh vào danh sách
            imageAdapter.notifyDataSetChanged();  // Cập nhật lại RecyclerView
        }
    }
    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
    //mở camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST); // Mở camera
    }

    private void uploadImageToStorage() {
        if (imageUris.isEmpty()) {
            Log.e("UploadImage", "No images selected.");
            return; // Nếu không có hình ảnh để upload
        }

        // Lấy userId để tạo đường dẫn
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        // Tạo StorageReference với đường dẫn theo yêu cầu
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("users").child(userId);

        // Tăng chỉ số folder mỗi lần chọn nhiều ảnh
        storageRef.listAll().addOnSuccessListener(listResult -> {
            // Lấy số folder hiện tại trong Firebase Storage
            int folderCount = listResult.getPrefixes().size() + 1;
            String folderName = "IdImgStt_" + folderCount; // Tạo tên thư mục mới dựa trên số lượng folder hiện tại
            StorageReference imageFolderRef = storageRef.child(folderName); // Tạo thư mục mới

            Log.d("UploadImage", "Creating folder: " + folderName); // Log để kiểm tra folderName

            int index = 1;
            for (Uri imageUri : imageUris) {
                // Tạo tên tệp mới cho từng ảnh
                String imageName = "image_" + index + ".jpg"; // Tên tệp mới
                StorageReference imageRef = imageFolderRef.child(imageName); // Tạo đường dẫn đến tệp mới

                // Upload ảnh từ URI
                uploadImage(imageUri, imageRef); // Upload ảnh vào tệp mới
                index++; // Tăng chỉ số
            }
        }).addOnFailureListener(exception -> {
            Log.e("UploadImage", "Failed to list files", exception);
        });

    }

    private void uploadImage(Uri imageUri, StorageReference storageRef) {
        // Upload ảnh từ URI
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Upload thành công
                    Log.d("UploadImage", "Image uploaded successfully to " + storageRef.getPath());
                }).addOnFailureListener(exception -> {
                    // Xử lý lỗi nếu upload thất bại
                    Log.e("UploadImage", "Failed to upload image", exception);
                });
    }

    private void setBtnCancel()
    {
        if(imageUris.size()>0)
        {
            imageUris.clear();
        }
        if(!edtContent.getText().toString().isEmpty())
        {
            edtContent.setText("");
        }
        imageAdapter.notifyDataSetChanged();

    }
    private void initId() {
        viewPager2 = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        btnCancel = findViewById(R.id.btnCancel);

        //init Quangkuns
        tv_nickname = findViewById(R.id.tvName);
        btnUpStatus = findViewById(R.id.btnUpStatus);
        edtContent = findViewById(R.id.tvDes);
        imvAvatar = findViewById(R.id.profileImage);
        chooseImage = findViewById(R.id.chooseImage);
        takeAPicture =findViewById(R.id.takeAPicture);
        recyclerViewImage = findViewById(R.id.recyclerView);
    }
}
