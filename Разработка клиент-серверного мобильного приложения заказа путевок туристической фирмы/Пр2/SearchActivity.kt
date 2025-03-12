package com.example.kp_luxurylife_part2

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.kp_luxurylife_part2.databinding.ActivitySearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var lastSearchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?.let {
            lastSearchQuery = it.getString("LAST_SEARCH_QUERY")
            binding.editTextSearch.setText(lastSearchQuery)
        }

        setupSearchField()
        setupButtons()
    }

    private fun setupSearchField() {
        binding.editTextSearch.hint = "Введите запрос"

        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            binding.buttonClear.visibility = if (hasFocus && !binding.editTextSearch.text.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        binding.editTextSearch.addTextChangedListener { text ->
            binding.buttonClear.visibility = if (!text.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        binding.editTextSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                performSearch(binding.editTextSearch.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun setupButtons() {
        binding.buttonClear.setOnClickListener {
            binding.editTextSearch.text.clear()
            binding.buttonClear.visibility = View.GONE
            hideKeyboard()
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonRetry.setOnClickListener {
            lastSearchQuery?.let {
                Toast.makeText(this, "Повторная попытка запроса...", Toast.LENGTH_SHORT).show()
                binding.editTextSearch.onEditorAction(EditorInfo.IME_ACTION_SEARCH)
            } ?: Toast.makeText(this, "Нет запроса для обновления", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            lastSearchQuery = query

            // Если введено слово "ошибка", имитируем ошибку
            if (query.equals("ошибка", ignoreCase = true)) {
                showErrorPlaceholder()
                return
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = FakeApiService.searchDestinations(query)
                    withContext(Dispatchers.Main) {
                        if (response.isNotEmpty()) {
                            displayResults(response)
                        } else {
                            showNoResultsPlaceholder()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showErrorPlaceholder()
                        Toast.makeText(this@SearchActivity, "Ошибка при выполнении запроса", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun displayResults(destinations: List<Destination>) {
        val resultText = destinations.joinToString("\n") { "${it.name} (${it.country}): ${it.description}" }
        binding.textViewResults.text = resultText
        binding.textViewResults.visibility = View.VISIBLE

        binding.textViewNoResults.visibility = View.GONE
        binding.textViewError.visibility = View.GONE
        binding.buttonRetry.visibility = View.GONE
    }

    private fun showNoResultsPlaceholder() {
        binding.textViewResults.visibility = View.GONE
        binding.textViewNoResults.visibility = View.VISIBLE
        binding.textViewError.visibility = View.GONE
        binding.buttonRetry.visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        binding.textViewResults.visibility = View.GONE
        binding.textViewNoResults.visibility = View.GONE
        binding.textViewError.visibility = View.VISIBLE
        binding.buttonRetry.visibility = View.VISIBLE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextSearch.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("LAST_SEARCH_QUERY", lastSearchQuery)
    }
}
