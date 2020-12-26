package tgo1014.instabox.presentation.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import tgo1014.instabox.managers.UserManager

class MainViewModel @ViewModelInject constructor(val userManager: UserManager) : ViewModel()