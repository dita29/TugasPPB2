package com.example.tugasppb2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.tugasppb2.databinding.ActivityLoginBinding
import com.example.tugasppb2.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var auth:FirebaseAuth? = null
    private val RC_SIGN_IN = 1
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener(this)
        binding.btnSimpan.setOnClickListener(this)
        binding.btnLihatdata.setOnClickListener(this)

        auth = FirebaseAuth.getInstance()
        setDataJabatan()
    }

    private fun setDataJabatan() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.Jabatan, R.layout.custom_spinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spJabatan.adapter = adapter
    }

    private fun isEmpty(s: String): Boolean {
        return TextUtils.isEmpty(s)
    }

    private fun jkel(): String {
        var jenkel = ""
        if (binding.rbLaki.isChecked) {
            jenkel = "Laki - Laki"
        } else {
            jenkel = "Perempuan"
        }
        return jenkel
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.btn_simpan -> {
                val getUserID = auth!!.currentUser!!.uid
                val database = FirebaseDatabase.getInstance()

                val getNama: String = binding.etNama.getText().toString()
                val getNip: String = binding.etNip.getText().toString()
                val getJkel: String = jkel()
                val getJabatan: String = binding.spJabatan.selectedItem.toString()

                val getReference: DatabaseReference
                getReference = database.reference

                if (isEmpty(getNama) || isEmpty(getNip) || isEmpty(getJkel) || isEmpty(getJabatan)) {
                    Toast.makeText(this@MainActivity, "Data Tidak Boleh Kosong Cuy", Toast.LENGTH_SHORT).show()
                } else {
                    getReference.child("Admin").child(getUserID).child("DataKaryawan").push()
                        .setValue(data_karyawan(getNama, getNip, getJkel, getJabatan))
                        .addOnCompleteListener(this) {
                            binding.etNama.setText("")
                            binding.etNip.setText("")
                            binding.rbLaki.isChecked = false
                            binding.rbPerempuan.isChecked = false
                            binding.spJabatan!!.setTag("")
                            Toast.makeText(this@MainActivity, "Data Sudah Tersimpan Cuy", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            R.id.btn_logout -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {
                            Toast.makeText(this@MainActivity, "Logout Berhasil Cuy", Toast.LENGTH_SHORT).show()
                            intent = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
            }
            R.id.btn_lihatdata -> {
                startActivity(Intent(this@MainActivity, MyListData::class.java))
            }
        }
    }
}