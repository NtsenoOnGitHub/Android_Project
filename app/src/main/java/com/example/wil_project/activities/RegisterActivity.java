package com.example.wil_project.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.example.wil_project.R;
import com.example.wil_project.data_models.Address;
import com.example.wil_project.data_models.Contact;
import com.example.wil_project.data_models.Documents;
import com.example.wil_project.data_models.Hitchhiker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    TextInputLayout lytGender, lytEmail, lytPassword, lytConfirmPassword, lytFirstName, lytLastName, lytId, lytCellNr,
            lytAlternativeCellNr, lytNokCellNr, lytCounty, lytProvince, lytCity, lytStreetName, lytHouseNumber, lytPostalCode;

    TextInputEditText edtEmail, edtPassword, edtConfirmPassword, edtFirstName, edtLastName, edtId, edtCellNr, edtAlternativeCellNr,
            edtNokCellNr, edtCountry, edtProvince, edtCity, edtStreetName, edtHouseNumber, edtPostalCode;

    AutoCompleteTextView dropdownGender;

    int UPLOAD_DONE_CODE = 0;

    Hitchhiker hitchhiker = new Hitchhiker();
    Address address = new Address();
    Contact contact = new Contact();
    Documents documents = new Documents();

    Random id = new Random();
    int operationResult = 0;

    public final int UPLOAD_PHOTO_CODE = 1, UPLOAD_ID_CODE = 2, UPLOAD_POA_CODE = 3, ACCESS_STORAGE_REQUEST_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Register");
        }

        edtEmail = findViewById(R.id.edtEmail); edtPassword = findViewById(R.id.edtPassword); edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtFirstName = findViewById(R.id.edtFirstName); edtLastName = findViewById(R.id.edtLastName); edtId = findViewById(R.id.edtId);
        edtCellNr = findViewById(R.id.edtCellNr); edtAlternativeCellNr = findViewById(R.id.edtAlternativeCellNr);
        edtNokCellNr = findViewById(R.id.edtNokCellNr); edtCountry = findViewById(R.id.edtCountry); edtProvince = findViewById(R.id.edtProvince);
        edtCity = findViewById(R.id.edtCity); edtStreetName = findViewById(R.id.edtStreetName); edtHouseNumber = findViewById(R.id.edtHouseNumber);
        edtPostalCode = findViewById(R.id.edtPostalCode);

        lytGender = findViewById(R.id.lytGender);
        dropdownGender = findViewById(R.id.dropdownGender);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(RegisterActivity.this, R.array.Gender_list, R.layout.menu_dropdown);
        adapter1.setDropDownViewResource(R.layout.menu_dropdown);
        dropdownGender.setAdapter(adapter1);

    }

    public void Register(View view) {

        if(edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty() || UPLOAD_DONE_CODE == 0 ||
        edtFirstName.getText().toString().isEmpty() || edtCellNr.getText().toString().isEmpty() || edtCountry.getText().toString().isEmpty()){

            Toast.makeText(this, "Please enter a all fields and upload all required documents", Toast.LENGTH_SHORT).show();
        }else {

            hitchhiker.setHitchhikerId(id.nextInt(1000000));
            documents.setHitchhikerId(hitchhiker.getHitchhikerId());
            //Check duplication

            BackendlessUser user = new BackendlessUser();
            user.setProperty("email", edtEmail.getText().toString());
            user.setPassword(edtPassword.getText().toString() );
            user.setProperty("UserId", hitchhiker.getHitchhikerId());
            user.setProperty("Role", "Hitchhiker");

            Backendless.UserService.register( user, new AsyncCallback<BackendlessUser>() {
                public void handleResponse( BackendlessUser registeredUser )
                {
                    // user has been registered and now can login
                }

                public void handleFault( BackendlessFault fault )
                {
                    // an error has occurred, the error code can be retrieved with fault.getCode()
                }
            } );

            // Client side validation

            hitchhiker.setUsername(edtEmail.getText().toString());
            hitchhiker.setPassword(edtPassword.getText().toString());
            hitchhiker.setName(edtFirstName.getText().toString());
            hitchhiker.setSurname(edtLastName.getText().toString());
            hitchhiker.setIdentityNumber(edtId.getText().toString());
            hitchhiker.setGender(dropdownGender.getText().toString());


            Backendless.Data.of( Hitchhiker.class ).save(hitchhiker, new AsyncCallback<Hitchhiker>() {
                public void handleResponse( Hitchhiker response )
                {
                    //Toast.makeText(RegisterActivity.this, ""+ response.getName() + " successfully registered", Toast.LENGTH_SHORT).show();
                    operationResult = 1;
                }

                public void handleFault( BackendlessFault fault )
                {
                    operationResult = 0;
                }
            });

            contact.setHitchhikerId(hitchhiker.getHitchhikerId());
            contact.setCellphoneNumber(edtCellNr.getText().toString());
            contact.setAlternativeNumber(edtAlternativeCellNr.getText().toString());
            contact.setNextOfKinCellphoneNumber(edtNokCellNr.getText().toString());

            Backendless.Data.of( Contact.class ).save( contact, new AsyncCallback<Contact>() {
                public void handleResponse( Contact response )
                {
                    operationResult = 1;
                }

                public void handleFault( BackendlessFault fault )
                {
                    // an error has occurred, the error code can be retrieved with fault.getCode()
                }
            });

            address.setHitchhikerId(hitchhiker.getHitchhikerId());
            address.setCountry(edtCountry.getText().toString());
            address.setProvince(edtProvince.getText().toString());
            address.setCity(edtCity.getText().toString());
            address.setStreetName(edtStreetName.getText().toString());
            address.setHouseNumber(Integer.parseInt(edtHouseNumber.getText().toString()));
            address.setPostalCode(Integer.parseInt(edtPostalCode.getText().toString()));

            Backendless.Data.of(Address.class ).save(address, new AsyncCallback<Address>() {
                public void handleResponse( Address response )
                {

                }

                public void handleFault( BackendlessFault fault )
                {
                    // an error has occurred, the error code can be retrieved with fault.getCode()
                }
            });

            Backendless.Data.of(Documents.class ).save(documents, new AsyncCallback<Documents>() {
                public void handleResponse( Documents response )
                {

                }

                public void handleFault( BackendlessFault fault )
                {
                    // an error has occurred, the error code can be retrieved with fault.getCode()
                }
            });

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            RegisterActivity.this.finish();
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
        }

    }

    public void UploadId(View view) {

        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    ACCESS_STORAGE_REQUEST_CODE);
        }
        else{
            Intent chooseFile = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(chooseFile, UPLOAD_ID_CODE);
            UPLOAD_DONE_CODE = 1;
        }
    }

    public void UploadPhoto(View view) {
        Intent chooseFile = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseFile, UPLOAD_PHOTO_CODE);
        UPLOAD_DONE_CODE = 1;
    }

    public void UploadPOA(View view) {

        Intent chooseFile = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseFile, UPLOAD_POA_CODE);
        UPLOAD_DONE_CODE = 1;
    }

    // for intents
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD_PHOTO_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
                String fileName = "JPEG_" + timeStamp + "_.jpg";
                Backendless.Files.Android.upload(photo,
                        Bitmap.CompressFormat.JPEG,
                        100,
                        fileName,
                        "Photos",
                        new AsyncCallback<BackendlessFile>() {
                            @Override
                            public void handleResponse(final BackendlessFile backendlessFile) {
                                hitchhiker.setPhotoUrl(backendlessFile.getFileURL());
                                Toast.makeText(RegisterActivity.this, "Photo document ready for upload", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(RegisterActivity.this, backendlessFault.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (IOException e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == UPLOAD_ID_CODE && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
                String fileName = "JPEG_" + timeStamp + "_.jpg";
                Backendless.Files.Android.upload(photo,
                        Bitmap.CompressFormat.JPEG,
                        100,
                        fileName,
                        "Documents",
                        new AsyncCallback<BackendlessFile>() {
                            @Override
                            public void handleResponse(final BackendlessFile backendlessFile) {
                                documents.setIdentityDocumentUrl(backendlessFile.getFileURL());
                                Toast.makeText(RegisterActivity.this, " ID document ready for upload", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(RegisterActivity.this, backendlessFault.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (IOException e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        if (requestCode == UPLOAD_POA_CODE && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();
            try {
                Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
                String fileName = "JPEG_" + timeStamp + "_.jpg";
                Backendless.Files.Android.upload(photo,
                        Bitmap.CompressFormat.JPEG,
                        100,
                        fileName,
                        "Documents",
                        new AsyncCallback<BackendlessFile>() {
                            @Override
                            public void handleResponse(final BackendlessFile backendlessFile) {
                                documents.setProofOfAddressUrl(backendlessFile.getFileURL());
                                Toast.makeText(RegisterActivity.this, "POA document is ready for upload", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                Toast.makeText(RegisterActivity.this, backendlessFault.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (IOException e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void Upload(){ //later on

    }
}