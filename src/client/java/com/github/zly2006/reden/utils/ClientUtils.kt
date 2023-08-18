package com.github.zly2006.reden.utils

import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text


fun buttonWidget(x: Int, y: Int, width: Int, height: Int, message: Text, onPress: ButtonWidget.PressAction) =
    ButtonWidget(x, y, width, height, message, onPress) { it.get() }
