package com.fall.control.ui.menu.menu_home

import com.fall.control.R
import com.fall.control.data.repository.Repository
import com.fall.control.ui.base.BaseViewModel

class MenuHomeViewModel(repository: Repository): BaseViewModel(repository) {
    override val fragmentId = R.id.menuHomeFragment
}