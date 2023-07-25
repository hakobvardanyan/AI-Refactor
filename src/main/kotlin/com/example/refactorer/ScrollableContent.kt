package com.example.refactorer

import javax.swing.*
import java.awt.*

class ScrollableContent : JComponent() {

    private val textArea: JTextArea = JTextArea()

    init {
        textArea.lineWrap = true // Enable word wrap
        textArea.wrapStyleWord = true // Wrap at word boundaries

        // Add some sample text
        val sampleText = "Click on the Run Refactoring Assistant to get suggestions"
        textArea.text = sampleText

        // Create a JScrollPane and add the JTextArea to it
        val scrollPane = JScrollPane(textArea)

        // Add the JScrollPane to this custom component
        layout = BorderLayout()
        add(scrollPane, BorderLayout.CENTER)
    }

    fun setText(text: String) {
        textArea.text = text
    }
}
