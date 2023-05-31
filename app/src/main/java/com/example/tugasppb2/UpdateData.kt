package com.example.tugasppb2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.tugasppb2.databinding.ActivityUpdateDataBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateData : AppCompatActivity() {
    private var database : DatabaseReference? = null
    private var auth : FirebaseAuth? = null
    private var cekNama : String? = null
    private var cekNIP : String? = null
    private var cekJabatan : String? = null
    private lateinit var binding: ActivityUpdateDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        data
        setDataJabatan()
        binding.btnUpdate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                cekNama = binding.newEtNama.getText().toString()
                cekNIP = binding.newEtNip.getText().toString()
                val cekJkel: String = jkel()
                cekJabatan = binding.newSpJabatan.selectedItem.toString()

                if (isEmpty(cekNama!!) || isEmpty(cekNIP!!) || isEmpty(cekJkel!!) || isEmpty(cekJabatan!!)) {
                    Toast.makeText(this@UpdateData, "Data Tidak Boleh Kosong Cuy", Toast.LENGTH_SHORT).show()
                } else {
                    val setdata_karyawan = data_karyawan()
                    setdata_karyawan.nama = binding.newEtNama.text.toString()
                    setdata_karyawan.nip = binding.newEtNip.text.toString()
                    setdata_karyawan.jkel = jkel()
                    setdata_karyawan.jabatan = binding.newSpJabatan.selectedItem.toString()
                    updateKaryawan(setdata_karyawan)
                }
            }
        })
    }

    private fun setDataJabatan() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.Jabatan, R.layout.custom_spinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.newSpJabatan.adapter = adapter
    }

    private fun isEmpty(s: String): Boolean {
        return TextUtils.isEmpty(s)
    }

    private fun jkel(): String{
        var jenkel = ""
        if (binding.newRbLaki.isChecked) {
            jenkel = "Laki - Laki"
        } else {
            jenkel = "Perempuan"
        }
        return jenkel
    }

    private val data: Unit
    private get() {
        val getNama = intent.extras!!.getString("dataNama")
        val getNIP = intent.extras!!.getString("dataNIP")
        val getJkel = intent.extras!!.getString("dataJkel")
        val getJabatan = intent.extras!!.getString("dataJabatan")

        binding.newEtNama!!.setText(getNama)
        binding.newEtNip!!.setText(getNIP)

        if (getJkel == "Laki - Laki"){
            binding.newRbLaki.isChecked = true
        } else {
            binding.newRbPerempuan.isChecked = true
        }

        binding.newSpJabatan!!.setTag(getJabatan)
    }

    private fun updateKaryawan(karyawan: data_karyawan) {
        val userID = auth!!.uid
        val getKey = intent.extras!!.getString("getPrimaryKey")
        database!!.child("Admin")
            .child(userID!!)
            .child("DataKaryawan")
            .child(getKey!!)
            .setValue(karyawan)
            .addOnSuccessListener {
                binding.newEtNama!!.setText("")
                binding.newEtNip!!.setText("")
                binding.newRbLaki.isChecked = false
                binding.newRbPerempuan.isChecked = false
                binding.newSpJabatan!!.setTag("")
                Toast.makeText(this,"Data Berhasil Diubah Cuy", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}