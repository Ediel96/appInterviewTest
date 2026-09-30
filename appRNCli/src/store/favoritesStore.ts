import {create} from 'zustand';
import {persist, createJSONStorage} from 'zustand/middleware';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {Product} from '../models/Product';

interface FavoritesState {
  favorites: Product[];
  hasHydrated: boolean;
  addFavorite: (product: Product) => void;
  removeFavorite: (productId: number) => void;
  toggleFavorite: (product: Product) => void;
  isFavorite: (productId: number) => boolean;
  setHasHydrated: (value: boolean) => void;
}

export const useFavoritesStore = create<FavoritesState>()(
  persist(
    (set, get) => ({
      favorites: [],
      hasHydrated: false,

      addFavorite: (product: Product) => {
        set(state => {
          // Check if already exists
          const exists = state.favorites.some(fav => fav.id === product.id);
          if (exists) {
            return state;
          }

          // Add new favorite
          return {
            favorites: [...state.favorites, product],
          };
        });
      },

      removeFavorite: (productId: number) => {
        set(state => ({
          favorites: state.favorites.filter(fav => fav.id !== productId),
        }));
      },

      toggleFavorite: (product: Product) => {
        const isFav = get().isFavorite(product.id);
        if (isFav) {
          get().removeFavorite(product.id);
        } else {
          get().addFavorite(product);
        }
      },

      isFavorite: (productId: number) => {
        return get().favorites.some(fav => fav.id === productId);
      },

      setHasHydrated: (value: boolean) => {
        set({hasHydrated: value});
      },
    }),
    {
      name: 'ministore-favorites',
      storage: createJSONStorage(() => AsyncStorage),
      onRehydrateStorage: () => state => {
        // Mark as hydrated when rehydration completes
        state?.setHasHydrated(true);
      },
      partialize: state => ({
        favorites: state.favorites,
      }),
    },
  ),
);
