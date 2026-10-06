import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  base: '/Al-Ghazi-Digital-Institute/',
  build: {
    outDir: 'dist'
  }
});
