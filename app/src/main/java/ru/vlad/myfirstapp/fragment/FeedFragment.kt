package ru.ваше_имя.myfirstapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.vlad.myfirstapp.R
import ru.vlad.myfirstapp.adapter.OnPostInteractionListener
import ru.vlad.myfirstapp.adapter.PostsAdapter
import ru.vlad.myfirstapp.databinding.FragmentFeedBinding
import ru.vlad.myfirstapp.dto.Post
import ru.vlad.myfirstapp.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels()

    private val interactionListener = object : OnPostInteractionListener {
        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onShare(post: Post) {
            val shareIntent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                putExtra(android.content.Intent.EXTRA_TEXT, post.content)
                type = "text/plain"
            }
            val chooserIntent = android.content.Intent.createChooser(shareIntent, getString(R.string.share_post_via))
            startActivity(chooserIntent)
            viewModel.shareById(post.id)
        }

        override fun onEdit(post: Post) {
            // Пока оставим заглушку, реализуем позже
            Toast.makeText(requireContext(), "Edit post ${post.id}", Toast.LENGTH_SHORT).show()
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
            Toast.makeText(requireContext(), R.string.remove_post, Toast.LENGTH_SHORT).show()
        }

        override fun onAvatarClick(post: Post) {
            Toast.makeText(requireContext(), "Profile: ${post.author}", Toast.LENGTH_SHORT).show()
            viewModel.increaseViews(post.id)
        }
        override fun onPostClick(post: Post) {
            // Переход к детальному фрагменту с передачей ID поста
            val bundle = Bundle().apply {
                putLong("postId", post.id)
            }
            findNavController().navigate(
                R.id.action_feedFragment_to_postDetailFragment,
                bundle
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostsAdapter(interactionListener)
        binding.list.adapter = adapter

        // Важно: используем viewLifecycleOwner для подписки
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        binding.fab.setOnClickListener {
            // Переход к созданию нового поста
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
