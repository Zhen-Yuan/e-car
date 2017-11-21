package com.example.denis.ecar.fragmente_Auto;

/**
 * Created by Raja on 20.11.2017.
 */

public class SelectedIdVariable {

        private int ivar = 0;
        private ChangeListener listener;

        public int getivar() {
            return ivar;
        }

        public void setivar(int var) {
            this.ivar = var;
            if (listener != null) listener.onChange();
        }

        public ChangeListener getListener() {
            return listener;
        }

        public void setListener(ChangeListener listener) {
            this.listener = listener;
        }

        public interface ChangeListener {
            void onChange();
        }

}
