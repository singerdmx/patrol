Blog::Application.routes.draw do
  resources :assets,   :defaults => { :format => 'json' }
  resources :check_routes,   :defaults => { :format => 'json' }
  resources :check_points,   :defaults => { :format => 'json' }

  devise_for :admins
  devise_for :users

  root "welcome#index"

  resources :admins, only: :index
end
