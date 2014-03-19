class Asset < ActiveRecord::Base
  has_and_belongs_to_many :check_routes
  has_many :check_points, dependent: :destroy
end
