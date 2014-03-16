class Asset < ActiveRecord::Base
  has_many :check_points, dependent: :destroy
end
