package com.codex.model

enum class NoteContentType(
    val serializedName: String,
    val displayName: String,
    val description: String
) {
    SimpleText(
        serializedName = "Simple Text",
        displayName = "Simple Text",
        description = "A note with just a text."
    ),
    SimpleImage(
        serializedName = "Simple Image",
        displayName = "Simple Image",
        description = "A note with just an image."
    ),
    Checkboxes(
        serializedName = "Checkboxes",
        displayName = "Checkboxes",
        description = "A note with checkboxes."
    ),
    ImageWithText(
        serializedName = "Image with Text",
        displayName = "Image with Text",
        description = "A note with an image and a text."
    ),
    ImageWithCheckboxes(
        serializedName = "Image with Checkboxes",
        displayName = "Image with Checkboxes",
        description = "A note with an image and checkboxes."
    )
}

fun String?.asNoteContentType() =
    NoteContentType.values()
        .firstOrNull { type ->
            type.serializedName == this
        } ?: NoteContentType.SimpleText