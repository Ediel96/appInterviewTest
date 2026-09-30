# Mini-Tienda de Productos - React Native

Aplicación móvil de catálogo de productos con funcionalidad de favoritos, desarrollada con React Native CLI y TypeScript.

## Objetivo

Implementar una aplicación móvil que consume el backend MiniStore API (Spring Boot) para mostrar productos, visualizar detalles y gestionar favoritos localmente con persistencia.

## Tecnologías

- **React Native**: 0.76.6
- **Node.js**: 20.x
- **TypeScript**: 5.0.4
- **React Navigation**: Stack y Bottom Tabs
- **Zustand**: Estado global con persistencia
- **AsyncStorage**: Almacenamiento local
- **Axios**: Cliente HTTP
- **Jest**: Testing
- **ESLint**: Linting

## Flujo de Datos

```
React Native → Spring Boot → DummyJSON
React Native → Zustand → AsyncStorage
```

**IMPORTANTE**: La aplicación móvil **NO** consume directamente DummyJSON. Todas las peticiones de productos se realizan exclusivamente al backend Spring Boot.

## Instalación

### Requisitos previos

- Node.js 20.x o superior
- Java Development Kit (JDK) 17
- Android Studio (para desarrollo Android)
- Xcode (para desarrollo iOS, solo en macOS)
- CocoaPods (para iOS)

### Instalar dependencias

```bash
npm install
```

### Configuración Android

El proyecto está listo para compilarse con Android. Asegúrate de tener:

- Android SDK instalado
- Variables de entorno configuradas (`ANDROID_HOME`)
- Un emulador Android configurado o un dispositivo físico conectado

### Configuración iOS

Solo en macOS. Instala las dependencias de CocoaPods:

```bash
npx pod-install
```

O manualmente:

```bash
cd ios
pod install
cd ..
```

## Configuración del Backend

La aplicación consume el backend en:

- **Android Emulator**: `http://10.0.2.2:8080`
- **iOS Simulator**: `http://localhost:8080`
- **Dispositivo físico**: `http://IP-LAN:8080`

La URL se configura en `src/config/environment.ts`. Para usar un dispositivo físico, actualiza la IP a la dirección local de tu computadora.

## Ejecución

### Iniciar Metro

En una terminal:

```bash
npm start
```

### Ejecutar en Android

En otra terminal:

```bash
npm run android
```

### Ejecutar en iOS

Solo en macOS:

```bash
npm run ios
```

### Otras opciones

```bash
npm run lint          # Ejecutar linter
npx tsc --noEmit      # Verificar tipos TypeScript
npm test              # Ejecutar pruebas
```

## Estructura del Proyecto

```
src/
├── api/              # Cliente HTTP y servicios
│   ├── httpClient.ts
│   └── productsApi.ts
├── components/       # Componentes reutilizables
│   ├── EmptyState.tsx
│   ├── ErrorView.tsx
│   ├── FavoriteButton.tsx
│   ├── LoadingView.tsx
│   ├── ProductCard.tsx
│   └── ProductCarousel.tsx
├── config/           # Configuración
│   └── environment.ts
├── hooks/            # Custom hooks
│   ├── useProduct.ts
│   └── useProducts.ts
├── models/           # Tipos TypeScript
│   ├── ApiError.ts
│   └── Product.ts
├── navigation/       # Navegación
│   ├── FavoritesStackNavigator.tsx
│   ├── ProductsStackNavigator.tsx
│   ├── RootNavigator.tsx
│   └── navigationTypes.ts
├── screens/          # Pantallas
│   ├── FavoritesScreen.tsx
│   ├── ProductDetailScreen.tsx
│   └── ProductsScreen.tsx
├── store/            # Estado global
│   └── favoritesStore.ts
├── theme/            # Tema
│   ├── colors.ts
│   ├── spacing.ts
│   └── typography.ts
└── utils/            # Utilidades
    ├── currency.ts
    └── errorMessage.ts
```

## Navegación

```
BottomTabNavigator
├── Productos (ProductsStack)
│   ├── ProductsList    → Lista de productos
│   └── ProductDetail   → Detalle del producto
└── Favoritos (FavoritesStack)
    ├── FavoritesList   → Lista de favoritos
    └── ProductDetail   → Detalle del producto
```

- **Productos**: Muestra la lista de productos obtenidos del backend
- **Favoritos**: Muestra productos guardados localmente
- **ProductDetail**: Accesible desde ambas pestañas, muestra información completa del producto

## Manejo de Errores

- **LoadingView**: Indicador de carga durante peticiones
- **ErrorView**: Mensaje de error con opción de reintentar
- **EmptyState**: Mensaje cuando no hay datos para mostrar
- **Validación defensiva**: Todas las respuestas del backend son validadas antes de usarse

## Estado Global

Zustand con persistencia en AsyncStorage:

```typescript
interface FavoritesState {
  favorites: Product[]
  hasHydrated: boolean
  addFavorite: (product: Product) => void
  removeFavorite: (productId: number) => void
  toggleFavorite: (product: Product) => void
  isFavorite: (productId: number) => boolean
}
```

- Los favoritos se guardan automáticamente
- Persisten después de cerrar la aplicación
- No se duplican IDs
- Hidratación controlada al iniciar

## Persistencia

Los favoritos se almacenan localmente con:

- **Zustand**: Manejo de estado
- **AsyncStorage**: Persistencia nativa
- **Clave**: `ministore-favorites`

Los favoritos **NO** se sincronizan con el backend. Son exclusivamente locales.

## Optimizaciones

### FlatList

- `keyExtractor` basado en ID
- `initialNumToRender`: 10
- `maxToRenderPerBatch`: 10
- `windowSize`: 10
- `ItemSeparatorComponent` estable
- `ListEmptyComponent` estable

### Renderizado

- `ProductCard` con `React.memo`
- `useCallback` para funciones en listas
- Selectores específicos de Zustand
- Evitar recreación de objetos de estilo

## Pruebas

Pruebas implementadas:

### Utilidades
- `currency.test.ts`: Formateo de precios
- `errorMessage.test.ts`: Extracción de mensajes de error

### Store
- `favoritesStore.test.ts`: Añadir, quitar, alternar favoritos, persistencia

### Componentes
- `ProductCard.test.tsx`: Renderizado de tarjeta
- `FavoriteButton.test.tsx`: Botón de favoritos

### Pantallas
- `ProductsScreen.test.tsx`: Lista de productos, estados
- `ProductDetailScreen.test.tsx`: Detalle, carga, errores
- `FavoritesScreen.test.tsx`: Lista de favoritos

Ejecutar pruebas:

```bash
npm test -- --runInBand
```

## Accesibilidad

- `accessibilityRole` en botones y elementos interactivos
- `accessibilityLabel` descriptivos
- `accessibilityHint` para acciones
- `accessibilityState` para favoritos seleccionados
- `maxFontSizeMultiplier` para limitar escalado de texto
- Áreas táctiles mínimas de 48x48
- Contraste adecuado de colores

## Decisiones Técnicas

### Por qué React Native CLI y no Expo

El proyecto base ya existía con React Native CLI. No se migró a Expo para mantener compatibilidad y evitar reescribir configuraciones nativas.

### Por qué Zustand

- Más simple que Redux
- Excelente integración con persistencia
- Menos boilerplate
- TypeScript nativo

### Por qué no React Query

Para este alcance, hooks personalizados con useState y useEffect son suficientes. React Query agrega complejidad innecesaria para un flujo simple de lectura.

### Validación defensiva

Todas las respuestas del backend se validan:
- `products` debe ser un arreglo
- `total` debe ser numérico
- `images` se convierte en arreglo vacío si no existe
- Errores controlados para respuestas inesperadas

### Carrusel personalizado

Se implementó con FlatList horizontal en lugar de una librería externa para:
- Mantener control total del comportamiento
- Evitar dependencias adicionales
- Aprovechar las optimizaciones de FlatList

## Limitaciones Conocidas

- Las pruebas con react-test-renderer tienen problemas de entorno en algunos casos
- No se implementó paginación (el backend devuelve todos los productos)
- No hay búsqueda o filtrado (fuera del alcance)
- No hay animaciones complejas (se evitó Reanimated por simplicidad)
- La URL del backend está hardcodeada por entorno

## Compilación

### Android

```bash
cd android
./gradlew clean assembleDebug
cd ..
```

El APK se genera en `android/app/build/outputs/apk/debug/`.

### iOS

Solo en macOS:

```bash
npx react-native run-ios --configuration Release
```

## Verificación Completa

```bash
npm run lint              # ✓ Linting
npx tsc --noEmit          # ✓ Tipos
npm test -- --runInBand   # ✓ Pruebas
cd android && ./gradlew assembleDebug  # ✓ Android
```

## Backend Requerido

La aplicación requiere que el backend MiniStore API esté ejecutándose en:

- `http://10.0.2.2:8080` (Android Emulator)
- `http://localhost:8080` (iOS Simulator)

Endpoints consumidos:

- `GET /api/products` - Lista de productos
- `GET /api/products/{id}` - Detalle de producto

## Licencia

Este proyecto es una prueba técnica.
