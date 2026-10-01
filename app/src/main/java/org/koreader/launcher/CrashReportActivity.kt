package org.koreader.launcher

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.koreader.launcher.databinding.CrashReportBinding
import java.io.File

class CrashReportActivity : AppCompatActivity() {
    private lateinit var binding: CrashReportBinding

    public override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.extras?.let { bundle ->
            binding = CrashReportBinding.inflate(layoutInflater)
            setContentView(binding.root)
            binding.title.text = bundle.get("title")?.toString() ?: "KOReader crashed"
            val reason = bundle.get("reason")?.toString() ?: ""
            binding.reason.text = reason
            if (reason.isNotEmpty()) {
                binding.reason.visibility = View.VISIBLE
            } else {
                binding.reason.visibility = View.GONE
            }

            var reportText = ""
            try {
                val reportFile = File(MainApp.crash_report_path)
                if (reportFile.exists()) {
                    reportText = reportFile.inputStream().bufferedReader().use { it.readText() }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (reportText.isNotEmpty()) {
                binding.logs.text = reportText
            } else if (reason.isNotEmpty()) {
                binding.logs.text = reason
            } else {
                binding.logs.text = "No detailed crash logs could be captured."
            }

            binding.shareReport.setOnClickListener {
                val intent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, binding.logs.text.toString())
                }
                startActivity(Intent.createChooser(intent,
                    resources.getString(R.string.test_share_rationale)))
            }
        } ?: noCrashReportAttachedError()
    }

    private fun noCrashReportAttachedError() {
        Toast.makeText(this,
            resources.getString(R.string.no_crash_attached), Toast.LENGTH_LONG).show()
        finish()
    }
}
