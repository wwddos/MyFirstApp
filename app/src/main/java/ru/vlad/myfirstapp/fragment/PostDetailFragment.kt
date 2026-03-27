package ru.vlad.myfirstapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.vlad.myfirstapp.R
import ru.vlad.myfirstapp.databinding.FragmentPostDetailBinding
import ru.vlad.myfirstapp.dto.Post
import ru.vlad.myfirstapp.viewmodel.PostViewModel
import java.text.DecimalFormat

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()
    private var currentPost: Post? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ID поста из аргументов
        val postId = arguments?.getLong("postId") ?: 0L

        // Наблюдаем за списком постов и находим нужный
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            currentPost = posts.find { it.id == postId }
            currentPost?.let { bindPost(it) }
        }

        setupClickListeners()
    }

    private fun bindPost(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content

            like.isChecked = post.likedByMe
            like.text = formatCount(post.likes)
            share.text = formatCount(post.shares)
            views.text = formatCount(post.views)
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            like.setOnClickListener {
                currentPost?.let { post ->
                    viewModel.likeById(post.id)
                }
            }

            share.setOnClickListener {
                currentPost?.let { post ->
                    val shareIntent = android.content.Intent().apply {
                        action = android.content.Intent.ACTION_SEND
                        putExtra(android.content.Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val chooserIntent = android.content.Intent.createChooser(
                        shareIntent,
                        getString(R.string.share_post_via)
                    )
                    startActivity(chooserIntent)
                    viewModel.shareById(post.id)
                }
            }

            avatar.setOnClickListener {
                currentPost?.let { post ->
                    Toast.makeText(requireContext(), "Profile: ${post.author}", Toast.LENGTH_SHORT).show()
                    viewModel.increaseViews(post.id)
                }
            }

            menu.setOnClickListener { view ->
                currentPost?.let { post -> showPopupMenu(view, post) }
            }
        }
    }

    private fun showPopupMenu(anchor: View, post: Post) {
        PopupMenu(requireContext(), anchor).apply {
            inflate(R.menu.post_menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        // Переход к редактированию
                        val bundle = Bundle().apply {
                            putString("postContent", post.content)
                        }
                        findNavController().navigate(
                            R.id.action_postDetailFragment_to_newPostFragment,
                            bundle
                        )
                        true
                    }
                    R.id.remove -> {
                        viewModel.removeById(post.id)
                        Toast.makeText(requireContext(), R.string.remove_post, Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> {
                val millions = count / 1_000_000.0
                if (millions % 1.0 == 0.0) {
                    "${millions.toInt()}M"
                } else {
                    DecimalFormat(".").format(millions) + "M"
                }
            }
            count >= 10_000 -> "${count / 1000}K"
            count >= 1_000 -> {
                val thousands = count / 1000.0
                if (thousands % 1.0 == 0.0) {
                    "${thousands.toInt()}K"
                } else {
                    DecimalFormat(".").format(thousands) + "K"
                }
            }
            else -> count.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val POST_ID_ARG = "postId"
    }
}
