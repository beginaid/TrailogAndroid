package com.codaid.trailogandroid.main.dash_board

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.EpoxyRecyclerView
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.RecyclerWeightWrapBindingModel_
import com.codaid.trailogandroid.RecyclerWorkoutBindingModel_
import com.codaid.trailogandroid.databinding.FragmentWorkoutBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WorkoutFragment : Fragment() {

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!
    private val db = Firebase.firestore
    private lateinit var userId: String
    lateinit var listener: AdapterView.OnItemClickListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSharedPreference()
        binding.loading.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            buildRecyclerView()
            binding.loading.visibility = View.GONE
        }
    }

    private suspend fun buildRecyclerView() {
        val allItems = mutableMapOf<String, MutableMap<String, MutableMap<String, String>>>()
        val colRefAlready = db.collection("workouts_$userId")
        colRefAlready.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val item = mutableMapOf<String, MutableMap<String, String>>()

                    @Suppress("UNCHECKED_CAST")
                    val contentsMap =
                        document.get("contents") as MutableMap<String, MutableMap<String, String>>
                    for (key in contentsMap.keys) {
                        val elem = mutableMapOf<String, String>()
                        elem["時間"] = contentsMap[key]?.get("時間").toString()
                        elem["平均心拍"] = contentsMap[key]?.get("平均心拍").toString()
                        elem["最大心拍"] = contentsMap[key]?.get("最大心拍").toString()
                        item[key] = elem
                    }
                    allItems[document.id] = item
                }
            }
            .addOnFailureListener { e ->
                Log.d("test", e.message.toString())
            }.await()

        if (allItems.isNotEmpty()) {
            binding.noDataText.visibility = View.GONE
            binding.parentRecycler.visibility = View.VISIBLE
            binding.parentRecycler.withModels {
                for (date in allItems.keys) {
                    RecyclerWeightWrapBindingModel_()
                        .id("parent_${date}")
                        .date(date.replace("-", "/"))
                        .onClickListener(View.OnClickListener { onSelected(date) })
                        .onBind { _, view, _ ->
                            buildChildRecyclerView(
                                view.dataBinding.root.findViewById(R.id.childRecycler),
                                allItems[date]
                            )
                        }
                        .addTo(this)
                }
            }
        } else {
            binding.parentRecycler.visibility = View.GONE
            binding.noDataText.visibility = View.VISIBLE
            binding.noDataText.text = getString(R.string.nodata_workout)
        }
    }

    private fun onSelected(date: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title)
            .setMessage(
                String.format(
                    getString(R.string.dialog_del_message),
                    date.replace("-", "/")
                )
            )
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                try {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.loading.visibility = View.VISIBLE
                        db.collection("workouts_$userId").document(date).delete().await()
                        Toast.makeText(
                            requireContext(),
                            R.string.dialog_del_complete,
                            Toast.LENGTH_SHORT
                        ).show()
                        buildRecyclerView()
                        binding.loading.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), R.string.dialog_del_failed, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton(R.string.dialog_cancel) { _, _ ->
            }
            .show()
    }

    private fun buildChildRecyclerView(
        childRecycler: EpoxyRecyclerView,
        item: MutableMap<String, MutableMap<String, String>>?
    ) {
        childRecycler.withModels {
            if (item != null) {
                for (event in item.keys) {
                    RecyclerWorkoutBindingModel_()
                        .id("child_${event}")
                        .event(event)
                        .minutes(item[event]?.get("時間") + " 分")
                        .avgBpm("平均" + item[event]?.get("平均心拍") + " bpm")
                        .maxBpm("最大" + item[event]?.get("最大心拍") + " bpm")
                        .addTo(this)
                }
            }
        }
    }

    private fun setSharedPreference() {
        val sharedPref = requireContext().getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        userId = sharedPref.getString(
            getString(R.string.saved_user_id),
            getString(R.string.default_user_id)
        )
            .toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}