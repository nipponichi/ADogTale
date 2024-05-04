package com.pmdm.adogtale.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pmdm.adogtale.R
import com.pmdm.adogtale.model.ChatMessageModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

// Declaración de la clase ChatRecyclerAdapter, que extiende FirestoreRecyclerAdapter parametrizado con el modelo ChatMessageModel y el ViewHolder ChatModelViewHolder
class ChatRecyclerAdapter// Asigna el contexto recibido a la variable de contexto
// Constructor de la clase ChatRecyclerAdapter que recibe FirestoreRecyclerOptions y un contexto
    (
    options: FirestoreRecyclerOptions<ChatMessageModel?>, // Declara una variable de contexto
    var context: Context
) :
    FirestoreRecyclerAdapter<ChatMessageModel, com.example.easychat.adapter.ChatRecyclerAdapter.ChatModelViewHolder>(
        options
    ) {
    // Método para enlazar datos a las vistas del ViewHolder
    protected override fun onBindViewHolder(
        holder: com.example.easychat.adapter.ChatRecyclerAdapter.ChatModelViewHolder,
        position: Int,
        model: ChatMessageModel
    ) {
        Log.i(
            "onBindViewHolder",
            "chatRecyclerAdapter"
        ) // Imprime un mensaje en el registro de eventos de Android
        // Comprueba si el remitente del mensaje es el usuario actual
        if (model.getSenderId().equals(FirebaseUtil.currentUserId())) {
            holder.leftChatLayout.setVisibility(View.GONE) // Oculta el diseño de chat izquierdo
            holder.rightChatLayout.setVisibility(View.VISIBLE) // Muestra el diseño de chat derecho
            holder.rightChatTextview.setText(model.getMessage()) // Establece el texto del mensaje en el TextView derecho
        } else {
            holder.rightChatLayout.setVisibility(View.GONE) // Oculta el diseño de chat derecho
            holder.leftChatLayout.setVisibility(View.VISIBLE) // Muestra el diseño de chat izquierdo
            holder.leftChatTextview.setText(model.getMessage()) // Establece el texto del mensaje en el TextView izquierdo
        }
    }

    // Método para crear un nuevo ViewHolder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): com.example.easychat.adapter.ChatRecyclerAdapter.ChatModelViewHolder {
        // Infla el diseño de fila de mensaje de chat y lo asigna a una nueva vista
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false)
        return com.example.easychat.adapter.ChatRecyclerAdapter.ChatModelViewHolder(view) // Devuelve una nueva instancia de ChatModelViewHolder con la vista inflada
    }

    // Clase interna ViewHolder para mantener las referencias a las vistas individuales del diseño de fila
    internal inner class ChatModelViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var leftChatLayout: LinearLayout
        var rightChatLayout // Declara los diseños de chat izquierdo y derecho como LinearLayout
                : LinearLayout
        var leftChatTextview: TextView
        var rightChatTextview // Declara los TextViews para los mensajes izquierdo y derecho
                : TextView

        // Constructor de la clase ChatModelViewHolder
        init {
            // Asigna las vistas de diseño de chat y TextViews a las variables correspondientes
            leftChatLayout = itemView.findViewById<LinearLayout>(R.id.left_chat_layout)
            rightChatLayout = itemView.findViewById<LinearLayout>(R.id.right_chat_layout)
            leftChatTextview = itemView.findViewById<TextView>(R.id.left_chat_textview)
            rightChatTextview = itemView.findViewById<TextView>(R.id.right_chat_textview)
        }
    }
}