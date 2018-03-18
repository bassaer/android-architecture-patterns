package com.github.bassaer.todomvp

/**
 * Created by nakayama on 2018/03/18.
 */
public interface BaseView<T> {
    fun setPresenter(presenter: T)
}