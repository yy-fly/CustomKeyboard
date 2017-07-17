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

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout

/**
 * 自定义数字键盘

 * @author : yyfly / developer@yyfly.com
 * *
 * @version : 1.0
 */
class CustomNumberKeyBoardView : LinearLayout {
    /**
     * 数字键盘
     */
    lateinit var numberKeyBoardView: NumberKeyBoardView
    /**
     * 自定义视图
     */
    lateinit var customView: LinearLayout


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val view = LayoutInflater.from(context).inflate(R.layout.ck_layout_custom_number_keyboard_view, this)
        val ll_keyboard = view.findViewById(R.id.ll_keyboard) as LinearLayout
        ll_keyboard.setOnTouchListener { v, event -> true }
        numberKeyBoardView = view.findViewById(R.id.number_keyboard_view) as NumberKeyBoardView
        customView = view.findViewById(R.id.custom_view) as LinearLayout
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    /**
     * 添加自定义视图到键盘
     * @param view
     */
    override fun addView(view: View) {
        customView.removeAllViews()
        customView.addView(view)
    }

    /**
     * EditText绑定自定义键盘

     * @param editText 需要绑定自定义键盘的EditText
     */
    fun attachTo(editText: EditText) {
        numberKeyBoardView.attachTo(editText)
    }

    /**
     * 设置按键事件
     * @param mOnKeyClick
     */
    fun setOnKeyClick(mOnKeyClick: OnKeyClick) {
        numberKeyBoardView.onKeyClick = mOnKeyClick
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

}
