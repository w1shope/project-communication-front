package com.example.matchingteam.activity.myinfo

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.matchingteam.R
import com.example.matchingteam.activity.HomeActivity
import com.example.matchingteam.activity.board.ReadBoardActivity
import com.example.matchingteam.api.board.BoardApi
import com.example.matchingteam.connection.RetrofitConnection
import com.example.matchingteam.databinding.ActivityBoardBinding
import com.example.matchingteam.databinding.ActivityEnrolBoardBinding
import com.example.matchingteam.databinding.ActivityEnrolBoardListBinding
import com.example.matchingteam.domain.board.Board
import com.example.matchingteam.dto.board.ListBoardDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.text.SimpleDateFormat

class EnrolBoardListActivity : AppCompatActivity() {
    lateinit var binding: ActivityEnrolBoardListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnrolBoardListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        boards(getLoginUserEmail()!!.trim())

        binding.buttonBoardList.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 모든 프로젝트 모집글 가져오기
     */
    private fun boards(email: String) {
        val retrofit = RetrofitConnection.getInstance()
        val api: BoardApi = retrofit.create(BoardApi::class.java)
        val call: Call<List<ListBoardDto>> = api.userBoards(email)
        call.enqueue(object : Callback<List<ListBoardDto>?> {
            override fun onResponse(
                call: Call<List<ListBoardDto>?>,
                response: Response<List<ListBoardDto>?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val boards: List<ListBoardDto>? = response.body()
                        if (boards != null) {
                            for (index in boards.indices) {
                                when (index) {
                                    0 -> dynamicLayout1(
                                        boards.get(index).title,
                                        boards.get(index).content,
                                        boards.get(index).createdDate,
                                        boards.get(index).viewCnt,
                                        boards.get(index).status
                                    )

                                    1 -> dynamicLayout2(
                                        boards.get(index).title,
                                        boards.get(index).content,
                                        boards.get(index).createdDate,
                                        boards.get(index).viewCnt,
                                        boards.get(index).status
                                    )

                                    else -> dynamicLayout3(
                                        boards.get(index).title,
                                        boards.get(index).content,
                                        boards.get(index).createdDate,
                                        boards.get(index).viewCnt,
                                        boards.get(index).status
                                    )
                                }
                            }

                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<ListBoardDto>?>, t: Throwable) {}
        })
    }

    /**
     * 특정 게시물 가져오기
     */
    private fun findBoard(title: String, content: String, createdDate: Timestamp, viewCnt: Int) {
        val retrofit = RetrofitConnection.getInstance()
        val api: BoardApi = retrofit.create(BoardApi::class.java)
        val call: Call<Board> = api.findBoard(title, content, createdDate, viewCnt)
        call.enqueue(object : Callback<Board> {
            override fun onResponse(call: Call<Board>, response: Response<Board>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val sp: SharedPreferences =
                            getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE)
                        val loginEmail: String? = sp.getString("loginEmail", null)
                        val intent =
                            Intent(this@EnrolBoardListActivity, ReadBoardActivity::class.java)
                        intent.putExtra("title", title)
                        intent.putExtra("content", content)
                        intent.putExtra("loginEmail", loginEmail)
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<Board>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    /**
     * Timestamp -> String
     */
    private fun timestampToStr(time: Timestamp): String {
        return SimpleDateFormat("yyyy.MM.dd").format(time)
    }

    private fun stringToTimestamp(time: String): Timestamp {
        return Timestamp.valueOf(time)
    }

    /**
     * 조회수 문자열 제거
     */
    private fun strToIntViewCnt(viewCnt: String): Int {
        return viewCnt.replace("[^0-9]".toRegex(), "").toInt()
    }

    private fun dynamicLayout1(
        title: String,
        content: String,
        createdDate: Timestamp,
        viewCnt: Int,
        statusId: Int
    ) {
        binding.mainLayout1.visibility = View.VISIBLE
        binding.textViewTitle1.text = title
        binding.textViewWriter1.text = "테스트"
        binding.textViewContent1.text = content
        binding.textViewCreatedDate1.text = "작성일자 : ${timestampToStr(createdDate)}"
        binding.textViewViewCnt1.text = "조회수 : ${viewCnt}"
        binding.hiddenCreatedDate1.text = createdDate.toString()
        binding.buttonStatus1.text =
            when (statusId) {
                0 -> "모집완료"
                else -> "모집중"
            }
    }

    private fun dynamicLayout2(
        title: String,
        content: String,
        createdDate: Timestamp,
        viewCnt: Int,
        statusId: Int
    ) {
        binding.mainLayout2.visibility = View.VISIBLE
        binding.textViewTitle2.text = title
        binding.textViewContent2.text = content
        binding.textViewCreatedDate2.text = "작성일자 : ${timestampToStr(createdDate)}"
        binding.textViewViewCnt2.text = "조회수 : ${viewCnt}"
        binding.hiddenCreatedDate2.text = createdDate.toString()
        binding.buttonStatus2.text =
            when (statusId) {
                0 -> "모집완료"
                else -> "모집중"
            }
    }

    private fun dynamicLayout3(
        title: String,
        content: String,
        createdDate: Timestamp,
        viewCnt: Int,
        statusId: Int
    ) {
        binding.mainLayout3.visibility = View.VISIBLE
        binding.textViewTitle3.text = title
        binding.textViewContent3.text = content
        binding.textViewCreatedDate3.text = "작성일자 : " + timestampToStr(createdDate)
        binding.textViewViewCnt3.text = "조회수 : ${viewCnt}"
        binding.hiddenCreatedDate3.text = createdDate.toString()
        binding.buttonStatus3.text =
            when (statusId) {
                0 -> "모집완료"
                else -> "모집중"
            }
    }

    private fun getLoginUserEmail(): String? {
        val sp: SharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE)
        return sp.getString("loginEmail", null)
    }
}