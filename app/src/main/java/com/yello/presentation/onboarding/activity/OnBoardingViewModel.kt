package com.yello.presentation.onboarding.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.domain.entity.onboarding.Friend
import com.example.domain.entity.onboarding.FriendGroup
import com.example.domain.entity.onboarding.FriendList
import com.example.domain.entity.onboarding.GroupList
import com.example.domain.entity.onboarding.SchoolList
import com.example.domain.entity.onboarding.SignupInfo
import com.example.domain.entity.onboarding.UserInfo
import com.example.domain.enum.GenderEnum
import com.example.domain.repository.OnboardingRepository
import com.example.ui.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) : ViewModel() {
    private val _postSignupState = MutableLiveData<UiState<UserInfo>>()
    val postSignupState: LiveData<UiState<UserInfo>>
        get() = _postSignupState

    private val _schoolData = MutableLiveData<UiState<SchoolList>>()
    val schoolData: MutableLiveData<UiState<SchoolList>> = _schoolData

    private val _departmentData = MutableLiveData<UiState<GroupList>>()
    val departmentData: MutableLiveData<UiState<GroupList>> = _departmentData

    private val _friendData = MutableLiveData<UiState<FriendList>>()
    val friendData: MutableLiveData<UiState<FriendList>> = _friendData

    private val _currentPage = MutableLiveData(0)
    val currentPage: LiveData<Int> = _currentPage

    var schoolPage = -1
    private var isSchoolPagingFinish = false
    private var totalSchoolPage = Integer.MAX_VALUE

    var departmentPage = -1
    private var isDepartmentPagingFinish = false
    private var totalDepartmentPage = Integer.MAX_VALUE

    var friendPage = -1L
    private var isFriendPagingFinish = false
    private var totalFriendPage = Long.MAX_VALUE

    val _school = MutableLiveData("")
    val school: String
        get() = _school.value?.trim() ?: ""

    var kakaoId: Int = -1
    var email: String = ""
    var profileImg: String = ""

    private val _groupId = MutableLiveData<Long>()
    val groupId: Long
        get() = requireNotNull(_groupId.value)

    val _department = MutableLiveData("")
    private val department: String
        get() = _department.value?.trim() ?: ""

    val _studentId = MutableLiveData<Int>()
    private val studentId: Int
        get() = requireNotNull(_studentId.value)

    val _name = MutableLiveData("")
    private val name: String
        get() = _name.value?.trim() ?: ""

    private val _friendList = MutableLiveData<FriendList>()
    val friendList: FriendList
        get() = _friendList.value ?: FriendList(0, emptyList())

    val _id = MutableLiveData("")
    private val id: String
        get() = _id.value?.trim() ?: ""

    private val _gender = MutableLiveData<String>()
    val gender: String
        get() = _gender.value ?: "MALE"

    private val _code = MutableLiveData("")
    val code: String
        get() = _code.value?.trim() ?: ""

    private val _profile = MutableLiveData("")
    val profile: String
        get() = _profile.value ?: ""

    private val _recommendId = MutableLiveData("")
    val recommendId: String
        get() = _recommendId.value ?: ""

    val isValidSchool: LiveData<Boolean> = _school.map { school -> checkValidSchool(school) }
    val isEmptyDepartment: LiveData<Boolean> =
        _department.map { department -> checkEmptyDepartment(department) }
    val isEmptyStudentId: LiveData<Boolean> =
        _studentId.map { studentId -> checkEmptyStudentId(studentId.toString()) }
    val isEmptyName: LiveData<Boolean> =
        _name.map { name -> checkEmptyName(name) }
    val isEmptyId: LiveData<Boolean> =
        _id.map { id -> checkEmptyId(id) }
    val isEmptyCode: LiveData<Boolean> =
        _code.map { code -> checkEmptyCode(code) }

    private val _studentIdResult: MutableLiveData<List<Int>> = MutableLiveData()
    val studentIdResult: LiveData<List<Int>> = _studentIdResult

    private val _friendResult: MutableLiveData<List<Friend>> = MutableLiveData()
    val friendResult: LiveData<List<Friend>> = _friendResult

    // TODO: throttle 및 페이징 처리
    fun getSchoolList(search: String) {
        Timber.d("GET SCHOOL LIST 메서드 호출 : $search")
        // if (isSchoolPagingFinish) return
        viewModelScope.launch {
            _schoolData.value = UiState.Loading
            onboardingRepository.getSchoolList(
                search,
                0,
                // ++schoolPage,
            ).onSuccess { schoolList ->
                Timber.d("GET SCHOOL LIST SUCCESS : $schoolList")
                if (schoolList == null) {
                    _schoolData.value = UiState.Empty
                    return@launch
                }
                // totalSchoolPage = ceil((schoolList.totalCount * 0.1)).toInt()
                // if (totalSchoolPage == schoolPage) isSchoolPagingFinish = true
                _schoolData.value =
                    when {
                        schoolList.schoolList.isEmpty() -> UiState.Empty
                        else -> UiState.Success(schoolList)
                    }
            }.onFailure { t ->
                if (t is HttpException) {
                    Timber.e("GET SCHOOL LIST FAILURE : $t")
                    _schoolData.value = UiState.Failure(t.code().toString())
                }
            }
        }
    }

    // TODO: throttle 및 페이징 처리
    fun getGroupList(search: String) {
        Timber.d("GET GROUP LIST 호출")
        // if (isDepartmentPagingFinish) return
        viewModelScope.launch {
            _departmentData.value = UiState.Loading
            onboardingRepository.getGroupList(
                school,
                search,
                ++departmentPage,
            ).onSuccess { groupList ->
                if (groupList == null) {
                    _departmentData.value = UiState.Empty
                    return@launch
                }

                // totalDepartmentPage = ceil((department.totalCount * 0.1)).toLong()
                // if (totalDepartmentPage == departmentPage) isDepartmentPagingFinish = true
                _departmentData.value =
                    when {
                        groupList.groupList.isEmpty() -> UiState.Empty
                        else -> UiState.Success(groupList)
                    }
            }.onFailure { t ->
                if (t is HttpException) {
                    Timber.e("GET GROUP LIST FAILURE : $t")
                    _departmentData.value = UiState.Failure(t.code().toString())
                }
                Timber.e("GET GROUP LIST ERROR : $t")
            }
        }
    }

    fun addListFriend(friendGroup: FriendGroup) {
        if (isFriendPagingFinish) return
        viewModelScope.launch {
            _friendData.value = UiState.Loading
            onboardingRepository.postFriendService(
                friendGroup,
                ++friendPage,
            ).onSuccess { friend ->
                if (friend == null) {
                    _friendData.value = UiState.Empty
                    return@launch
                }
                totalFriendPage = kotlin.math.ceil((friend.totalCount * 0.1)).toLong()
                if (totalFriendPage == friendPage) isFriendPagingFinish = true
                _friendData.value =
                    when {
                        friend.friendList.isEmpty() -> UiState.Empty
                        else -> UiState.Success(friend)
                    }
            }
        }
    }

    fun postSignup() {
        viewModelScope.launch {
            val signupInfo = SignupInfo(
                kakaoId = kakaoId,
                email = email,
                profileImg = profileImg,
                groupId = groupId,
                studentId = studentId,
                name = name,
                yelloId = id,
                gender = gender,
                friendList = friendList.toIdList(),
                recommendId = recommendId,
            )
            onboardingRepository.postSignup(signupInfo)
                .onSuccess { userInfo ->
                    Timber.d("POST SIGN UP SUCCESS : $userInfo")
                    if (userInfo == null) {
                        _postSignupState.value = UiState.Empty
                        return@launch
                    }

                    _postSignupState.value = UiState.Success(userInfo)
                }
                .onFailure { t ->
                    if (t is HttpException) {
                        Timber.e("POST SIGN UP FAILURE : $t")
                        _postSignupState.value = UiState.Failure(t.code().toString())
                        return@launch
                    }
                    Timber.e("POST SIGN UP ERROR : $t")
                }
        }
    }

    fun setSchool(school: String) {
        _school.value = school
    }

    fun setGroupInfo(department: String, groupId: Long) {
        _department.value = department
        _groupId.value = groupId
    }

    fun setStudentId(studentId: Int) {
        _studentId.value = studentId
    }

    fun clearSchoolData() {
        _schoolData.value = UiState.Success(SchoolList(0, emptyList()))
    }

    fun cleaDepartmentData() {
        _departmentData.value = UiState.Success(GroupList(0, emptyList()))
    }

    private fun checkValidSchool(school: String): Boolean {
        return school.isNotBlank()
    }

    private fun checkEmptyDepartment(department: String): Boolean {
        return department.isBlank()
    }

    private fun checkEmptyStudentId(studentId: String): Boolean {
        return studentId.isBlank()
    }

    private fun checkEmptyName(name: String): Boolean {
        return name.isBlank()
    }

    private fun checkRegexName(name: String): Boolean {
        return name.matches("^[ㄱ-ㅎㅏ-ㅣ가-힣]\$".toRegex())
    }

    private fun checkRegexId(id: String): Boolean {
        return id.matches("^[A-Za-z0-9_.]*\$".toRegex())
    }

    private fun checkEmptyId(id: String): Boolean {
        return id.isBlank()
    }

    private fun checkEmptyCode(code: String): Boolean {
        return code.isBlank()
    }

    fun navigateToNextPage() {
        _currentPage.value = currentPage.value?.plus(1)
    }

    fun navigateToBackPage() {
        _currentPage.value = currentPage.value?.minus(1)
    }

    fun addStudentId() {
        val mockList = listOf(15, 16, 17, 18, 19, 20, 21, 22, 23)
        _studentIdResult.value = mockList
    }
}
