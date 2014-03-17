Blog::Application.routes.draw do



 # root "welcome#index"
  resources :assets,   :defaults => { :format => 'json' }
  resources :check_routes,   :defaults => { :format => 'json' }
  resources :check_points,   :defaults => { :format => 'json' }
end
