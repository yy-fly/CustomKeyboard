/*
 * Copyright (C) 2017 yyfly, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yyfly.android.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.inputmethodservice.Keyboard
import android.inputmethodservice.Keyboard.Key
import android.inputmethodservice.KeyboardView
import android.os.Build
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.LinkedList
import java.util.Random

/**
 * 数字键盘

 * @author : yyfly / developer@yyfly.com
 * *
 * @version : 1.0
 */
class NumberKeyBoardView : KeyboardView {
    /**
     * 数字键盘
     */
    private var mKeyBoard: Keyboard? = null
    private var mEditText: EditText? = null
    var onKeyClick: OnKeyClick? = null
    var isRandom: Boolean = false

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mKeyBoard = Keyboard(context, R.xml.ck_keyboard_number)
    }

    /**
     * 重新画一些按键
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mKeyBoard = this.keyboard
        var keys: List<Key>? = null
        if (mKeyBoard != null) {
            keys = mKeyBoard!!.keys
        }

        if (keys != null) {
            for (key in keys) {
                // 数字键盘的处理
                if (key.codes[0] == -4) {
                    drawKeyBackground(R.drawable.ck_selector_key_ok, canvas, key)
                    drawText(canvas, key)
                }
            }
        }
    }

    /**
     * 绘制键盘背景

     * @param drawableId
     * *
     * @param canvas
     * *
     * @param key
     */
    private fun drawKeyBackground(drawableId: Int, canvas: Canvas, key: Key) {
        val npd = resources.getDrawable(drawableId)
        val drawableState = key.currentDrawableState
        if (key.codes[0] != 0) {
            npd.state = drawableState
        }
        npd.setBounds(key.x, key.y, key.x + key.width, key.y + key.height)
        npd.draw(canvas)
    }

    private fun drawText(canvas: Canvas, key: Key) {
        val bounds = Rect()
        val paint = Paint()
        paint.textAlign = Paint.Align.CENTER

        paint.isAntiAlias = true
        paint.color = Color.WHITE
        if (key.label != null) {
            val label = key.label.toString()

            val field: Field

            if (label.length > 1 && key.codes.size < 2) {
                var labelTextSize = 0
                try {
                    field = KeyboardView::class.java.getDeclaredField("mLabelTextSize")
                    field.isAccessible = true
                    labelTextSize = field.get(this) as Int
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }

                paint.textSize = labelTextSize.toFloat()
                paint.typeface = Typeface.DEFAULT_BOLD
            } else {
                var keyTextSize = 0
                try {
                    field = KeyboardView::class.java.getDeclaredField("mLabelTextSize")
                    field.isAccessible = true
                    keyTextSize = field.get(this) as Int
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }

                paint.textSize = keyTextSize.toFloat()
                paint.typeface = Typeface.DEFAULT
            }

            paint.getTextBounds(key.label.toString(), 0, key.label.toString().length, bounds)
            canvas.drawText(key.label.toString(), (key.x + key.width / 2).toFloat(), (key.y + key.height / 2 + bounds.height() / 2).toFloat(), paint)
        } else if (key.icon != null) {
            key.icon.setBounds(key.x + (key.width - key.icon.intrinsicWidth) / 2, key.y + (key.height - key.icon.intrinsicHeight) / 2,
                    key.x + (key.width - key.icon.intrinsicWidth) / 2 + key.icon.intrinsicWidth, key.y + (key.height - key.icon.intrinsicHeight) / 2 + key.icon.intrinsicHeight)
            key.icon.draw(canvas)
        }

    }

    /**
     * EditText绑定自定义键盘

     * @param editText 需要绑定自定义键盘的EditText
     */
    fun attachTo(editText: EditText) {
        this.mEditText = editText
        hideSystemSoftKeyboard(context, mEditText!!)
        showSoftKeyboard()
    }

    /**
     * 显示键盘
     */
    fun showSoftKeyboard() {
        if (mKeyBoard == null) {
            mKeyBoard = Keyboard(context, R.xml.ck_keyboard_number)
        }
        if (isRandom) {
            randomKeyboardNumber()
        } else {
            keyboard = mKeyBoard
        }
        isEnabled = true
        isPreviewEnabled = false
        visibility = View.VISIBLE
        onKeyboardActionListener = mOnKeyboardActionListener
    }

    /**
     * 按键事件
     */
    private val mOnKeyboardActionListener = object : KeyboardView.OnKeyboardActionListener {
        override fun onPress(primaryCode: Int) {

        }

        override fun onRelease(primaryCode: Int) {

        }

        override fun onKey(primaryCode: Int, keyCodes: IntArray) {
            val editable = mEditText!!.text
            val start = mEditText!!.selectionStart
            if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                if (editable != null && editable.length > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start)
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 隐藏键盘
                hideKeyboard()
                if (onKeyClick != null) {
                    onKeyClick!!.onCancelClick()
                }
            } else if (primaryCode == Keyboard.KEYCODE_DONE) {// 隐藏键盘
                hideKeyboard()
                if (onKeyClick != null) {
                    onKeyClick!!.onOkClick()
                }
            } else {
                editable!!.insert(start, Character.toString(primaryCode.toChar()))
            }
        }

        override fun onText(text: CharSequence) {

        }

        override fun swipeLeft() {

        }

        override fun swipeRight() {

        }

        override fun swipeDown() {

        }

        override fun swipeUp() {

        }
    }

    /**
     * 判断是否是数字

     * @param str
     * *
     * @return
     */
    private fun isNumber(str: String): Boolean {
        val numString = "0123456789"
        return numString.contains(str)
    }

    /**
     * 数字随机排序
     */
    private fun randomKeyboardNumber() {
        val keyList = mKeyBoard!!.keys
        // 查找出0-9的数字键
        val newKeyList = ArrayList<Key>()
        for (i in keyList.indices) {
            if (keyList[i].label != null && isNumber(keyList[i].label.toString())) {
                newKeyList.add(keyList[i])
            }
        }
        // 数组长度
        val count = newKeyList.size
        // 结果集
        val resultList = ArrayList<KeyModel>()
        // 用一个LinkedList作为中介
        val temp = LinkedList<KeyModel>()
        // 初始化temp
        for (i in 0..count - 1) {
            temp.add(KeyModel(48 + i, i.toString() + ""))
        }
        // 取数
        val rand = Random()
        for (i in 0..count - 1) {
            val num = rand.nextInt(count - i)
            resultList.add(KeyModel(temp[num].code, temp[num].label))
            temp.removeAt(num)
        }
        for (i in newKeyList.indices) {
            newKeyList[i].label = resultList[i].label
            newKeyList[i].codes[0] = resultList[i].code!!
        }

        keyboard = mKeyBoard
    }

    /**
     * 显示键盘
     */
    fun showKeyboard() {
        val visibility = visibility
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            setVisibility(View.VISIBLE)
        }
    }

    /**
     * 隐藏键盘
     */
    fun hideKeyboard() {
        val visibility = visibility
        if (visibility == View.VISIBLE) {
            setVisibility(View.GONE)
        }
    }

    companion object {

        /**
         * 隐藏系统键盘

         * @param editText
         */
        fun hideSystemSoftKeyboard(context: Context, editText: EditText) {
            val sdkInt = Build.VERSION.SDK_INT
            if (sdkInt >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                try {
                    val cls = EditText::class.java
                    val setShowSoftInputOnFocus: Method
                    setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
                    setShowSoftInputOnFocus.isAccessible = true
                    setShowSoftInputOnFocus.invoke(editText, false)

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                editText.inputType = InputType.TYPE_NULL
            }
            // 如果软键盘已经显示，则隐藏
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }
}
